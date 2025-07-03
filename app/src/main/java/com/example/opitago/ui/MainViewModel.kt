// En el archivo ui/MainViewModel.kt
package com.example.opitago.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opitago.data.Ruta
import com.example.opitago.data.RutaAgrupada
import com.example.opitago.data.RutaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val repository: RutaRepository) : ViewModel() {

    private val _terminoDeBusqueda = MutableStateFlow("")
    val terminoDeBusqueda: StateFlow<String> = _terminoDeBusqueda

    private val _filtroActivo = MutableStateFlow<String?>(null)
    val filtroActivo: StateFlow<String?> = _filtroActivo

    fun enBusquedaCambiada(query: String) {
        _terminoDeBusqueda.value = query
        if (query.isBlank()) {
            _filtroActivo.value = null
        }
    }

    fun setFiltroActivo(filtro: String) {
        if (_filtroActivo.value == filtro) {
            _terminoDeBusqueda.value = ""
            _filtroActivo.value = null
        } else {
            _terminoDeBusqueda.value = filtro
            _filtroActivo.value = filtro
        }
    }


    val rutasVisibles: StateFlow<List<MainScreenItem>> = _terminoDeBusqueda
        .flatMapLatest { query ->
            val flowDeRutas = if (query.isBlank()) {
                repository.todasLasRutas
            } else {
                repository.buscarRutas("%$query%")
            }


            flowDeRutas.map { listaPlanaDeRutas ->
                if (query.isNotBlank()) {

                    crearListaPlanaAgrupada(listaPlanaDeRutas)
                } else {

                    crearListaAgrupadaPorComuna(listaPlanaDeRutas)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )


    private fun crearListaAgrupadaPorComuna(rutas: List<Ruta>): List<MainScreenItem> {
        val itemsFinales = mutableListOf<MainScreenItem>()


        val rutasPorComuna = mutableMapOf<String, MutableList<Ruta>>()
        rutas.forEach { ruta ->
            ruta.comuna.split(",").forEach { comunaStr ->
                val comunaLimpia = comunaStr.trim()
                if (comunaLimpia.isNotEmpty()) {
                    rutasPorComuna.getOrPut(comunaLimpia) { mutableListOf() }.add(ruta)
                }
            }
        }


        val comunasOrdenadas = rutasPorComuna.keys.sortedWith(
            compareBy(
                { it.toIntOrNull() ?: Int.MAX_VALUE },
                { it }
            )
        )


        comunasOrdenadas.forEach { comuna ->
            itemsFinales.add(HeaderItem(comuna))
            val rutasDeLaComuna = rutasPorComuna[comuna] ?: emptyList()

            val listaAgrupada = rutasDeLaComuna
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
            itemsFinales.addAll(listaAgrupada.map { RutaItem(it) })
        }

        return itemsFinales
    }


    private fun crearListaPlanaAgrupada(rutas: List<Ruta>): List<MainScreenItem> {
        return rutas
            .groupBy { it.nombre }
            .map { (nombre, rutasDelGrupo) ->
                RutaItem(
                    RutaAgrupada(
                        nombre = nombre,
                        numeroAntiguo = rutasDelGrupo.firstNotNullOfOrNull { it.numeroAntiguo },
                        rutaIda = rutasDelGrupo.find { it.sentido == "Ida" || it.sentido == "Único" },
                        rutaVuelta = rutasDelGrupo.find { it.sentido == "Vuelta" },
                        isExpanded = false
                    )
                )
            }
    }
}