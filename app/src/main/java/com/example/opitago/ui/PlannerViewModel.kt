package com.example.opitago.ui

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opitago.data.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class PlannerState {
    object Idle : PlannerState()
    object Loading : PlannerState()
    data class Success(val resultados: List<RutaSugerida>) : PlannerState()
    data class Error(val message: String) : PlannerState()
}


private data class ClosestPointInfo(
    val ruta: Ruta,
    val closestPointIndex: Int,
    val distance: Float
)




class PlannerViewModel : ViewModel() {

    private val _plannerState = MutableStateFlow<PlannerState>(PlannerState.Idle)
    val plannerState: StateFlow<PlannerState> = _plannerState

    private lateinit var repository: RutaRepository
    private var todasLasRutas: List<Ruta> = emptyList()
    private val TAG = "PlannerViewModel"

    fun init(context: Context) {
        repository = RutaRepository(OpitaGODatabase.getInstance(context).rutaDao())
        viewModelScope.launch(Dispatchers.IO) {
            todasLasRutas = repository.obtenerTodasLasRutasSync()
            Log.d(TAG, "Se cargaron ${todasLasRutas.size} rutas en total.")
        }
    }

    fun calcularMejorRuta(origen: LatLng, destino: LatLng) {
        _plannerState.value = PlannerState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val origenLocation = Location("").apply { latitude = origen.latitude; longitude = origen.longitude }
            val destinoLocation = Location("").apply { latitude = destino.latitude; longitude = destino.longitude }

            Log.d(TAG, "Calculando ruta desde: $origenLocation hasta $destinoLocation")

            val rutasCercanasOrigen = encontrarRutasCercanasConInfo(origenLocation)
            val rutasCercanasDestino = encontrarRutasCercanasConInfo(destinoLocation)

            Log.d(TAG, "Rutas cercanas al origen (${rutasCercanasOrigen.size}): ${rutasCercanasOrigen.map { it.ruta.nombre }}")
            Log.d(TAG, "Rutas cercanas al destino (${rutasCercanasDestino.size}): ${rutasCercanasDestino.map { it.ruta.nombre }}")

            val rutasDirectas = mutableListOf<RutaSugeridaDirecta>()
            for (infoOrigen in rutasCercanasOrigen) {
                for (infoDestino in rutasCercanasDestino) {
                    if (infoOrigen.ruta.id == infoDestino.ruta.id && infoOrigen.closestPointIndex < infoDestino.closestPointIndex) {
                        Log.d(TAG, "Ruta directa encontrada y en dirección correcta: ${infoOrigen.ruta.nombre}")
                        val rutaAgrupada = agruparRutas(listOf(infoOrigen.ruta)).first()
                        if (rutasDirectas.none { it.ruta.nombre == rutaAgrupada.nombre }) {
                            rutasDirectas.add(RutaSugeridaDirecta(rutaAgrupada))
                        }
                    }
                }
            }

            if (rutasDirectas.isNotEmpty()) {
                Log.d(TAG, "Enviando ${rutasDirectas.size} rutas directas a la UI.")
                _plannerState.value = PlannerState.Success(rutasDirectas)
                return@launch
            }

            Log.d(TAG, "No hay rutas directas. Buscando transbordos...")
            val posiblesTransbordos = buscarTransbordos(rutasCercanasOrigen, rutasCercanasDestino)

            if(posiblesTransbordos.isNotEmpty()){
                Log.d(TAG, "Enviando ${posiblesTransbordos.size} rutas con transbordo a la UI.")
                _plannerState.value = PlannerState.Success(posiblesTransbordos)
                return@launch
            }

            Log.d(TAG, "No se encontró ninguna ruta directa ni con transbordo.")
            _plannerState.value = PlannerState.Success(emptyList())
        }
    }

    private fun buscarTransbordos(
        rutasOrigen: List<ClosestPointInfo>,
        rutasDestino: List<ClosestPointInfo>
    ): List<RutaSugeridaTransbordo> {
        val transbordosEncontrados = mutableListOf<RutaSugeridaTransbordo>()
        val TRANSFER_DISTANCE_METERS = 350.0

        for (infoOrigen in rutasOrigen) {
            for (infoDestino in rutasDestino) {
                if (infoOrigen.ruta.nombre == infoDestino.ruta.nombre) continue

                val polylineOrigen = parsearCoordenadas(infoOrigen.ruta.coordenadas)
                val polylineDestino = parsearCoordenadas(infoDestino.ruta.coordenadas)

                for ((indexOrigen, puntoOrigen) in polylineOrigen.withIndex()) {
                    if (indexOrigen < infoOrigen.closestPointIndex) continue

                    for ((indexDestino, puntoDestino) in polylineDestino.withIndex()) {
                        if (indexDestino > infoDestino.closestPointIndex) continue

                        if (puntoOrigen.distanceTo(puntoDestino) < TRANSFER_DISTANCE_METERS) {
                            Log.d(TAG, "¡Posible transbordo! Entre ${infoOrigen.ruta.nombre} y ${infoDestino.ruta.nombre}")
                            val rutaInicialAgrupada = agruparRutas(listOf(infoOrigen.ruta)).first()
                            val rutaFinalAgrupada = agruparRutas(listOf(infoDestino.ruta)).first()
                            transbordosEncontrados.add(
                                RutaSugeridaTransbordo(
                                    rutaInicial = rutaInicialAgrupada,
                                    rutaFinal = rutaFinalAgrupada,
                                    puntoTransbordo = "Caminar ${puntoOrigen.distanceTo(puntoDestino).toInt()}m"
                                )
                            )
                            return transbordosEncontrados
                        }
                    }
                }
            }
        }
        return transbordosEncontrados
    }

    private fun encontrarRutasCercanasConInfo(punto: Location): List<ClosestPointInfo> {
        val rutasCercanas = mutableListOf<ClosestPointInfo>()
        val WALKING_DISTANCE_METERS = 500.0

        todasLasRutas.forEach { ruta ->
            val coordenadasRuta = parsearCoordenadas(ruta.coordenadas)
            if (coordenadasRuta.isEmpty()) return@forEach

            var distanciaMinima = Float.MAX_VALUE
            var indiceMasCercano = -1

            coordenadasRuta.forEachIndexed { index, coordenada ->
                val distancia = punto.distanceTo(coordenada)
                if (distancia < distanciaMinima) {
                    distanciaMinima = distancia
                    indiceMasCercano = index
                }
            }

            if (distanciaMinima <= WALKING_DISTANCE_METERS) {
                rutasCercanas.add(
                    ClosestPointInfo(
                        ruta = ruta,
                        closestPointIndex = indiceMasCercano,
                        distance = distanciaMinima
                    )
                )
            }
        }
        return rutasCercanas
    }

    private fun parsearCoordenadas(coordsStr: String): List<Location> {
        return coordsStr.split(" ")
            .mapNotNull { pointStr ->
                val lngLatAlt = pointStr.split(",")
                if (lngLatAlt.size >= 2) {
                    Location("").apply {
                        longitude = lngLatAlt[0].toDoubleOrNull() ?: 0.0
                        latitude = lngLatAlt[1].toDoubleOrNull() ?: 0.0
                    }
                } else null
            }
    }

    private fun agruparRutas(rutas: List<Ruta>): List<RutaAgrupada> {
        return rutas
            .groupBy { it.nombre }
            .map { (nombre, rutasDelGrupo) ->
                RutaAgrupada(
                    nombre = nombre,
                    numeroAntiguo = rutasDelGrupo.firstNotNullOfOrNull { it.numeroAntiguo },
                    rutaIda = rutasDelGrupo.find { it.sentido == "Ida" || it.sentido == "Único" },
                    rutaVuelta = rutasDelGrupo.find { it.sentido == "Vuelta" },
                    isExpanded = false
                )
            }
    }
}