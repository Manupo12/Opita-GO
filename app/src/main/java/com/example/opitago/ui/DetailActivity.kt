package com.example.opitago.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.opitago.R
import com.example.opitago.data.OpitaGODatabase
import com.example.opitago.data.Ruta
import com.example.opitago.data.RutaRepository
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val detailViewModel: DetailViewModel by viewModels {
        val database = OpitaGODatabase.getInstance(applicationContext)
        val repository = RutaRepository(database.rutaDao())
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Recibimos el nombre de la ruta, ej: "247"
        val nombreRuta = intent.getStringExtra("EXTRA_RUTA_NOMBRE")

        if (nombreRuta != null) {
            // Le pedimos al ViewModel que cargue los datos
            detailViewModel.cargarRutasPorNombre(nombreRuta)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Observamos los datos del ViewModel
        observarRutas()
    }

    private fun observarRutas() {
        lifecycleScope.launch {
            detailViewModel.rutas.collect { listaDeRutas ->
                if (listaDeRutas.isNotEmpty()) {
                    dibujarRutasEnMapa(listaDeRutas)
                }
            }
        }
    }

    private fun dibujarRutasEnMapa(rutas: List<Ruta>) {
        map.clear() // Limpiamos el mapa de dibujos anteriores
        val boundsBuilder = LatLngBounds.Builder()

        rutas.forEach { ruta ->
            val puntosDeRuta = parseCoordenadas(ruta.coordenadas)
            if (puntosDeRuta.isNotEmpty()) {
                val colorDeRuta = if (ruta.sentido == "Ida") Color.GREEN else Color.RED
                val polylineOptions = PolylineOptions()
                    .addAll(puntosDeRuta)
                    .color(colorDeRuta)
                    .width(12f)

                map.addPolyline(polylineOptions)

                // Añadimos todos los puntos al constructor de límites para hacer zoom
                puntosDeRuta.forEach { punto ->
                    boundsBuilder.include(punto)
                }
            }
        }

        // Hacemos que la cámara haga zoom para que se vean todas las rutas dibujadas
        if (rutas.any { it.coordenadas.isNotBlank() }) {
            val bounds = boundsBuilder.build()
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)) // 100 es el padding
        }
    }

    private fun parseCoordenadas(coordenadasStr: String): List<LatLng> {
        val listaDePuntos = mutableListOf<LatLng>()
        val puntos = coordenadasStr.trim().split(Regex("\\s+"))
        for (punto in puntos) {
            val latLon = punto.split(",")
            if (latLon.size >= 2) {
                try {
                    val lat = latLon[1].toDouble()
                    val lon = latLon[0].toDouble()
                    listaDePuntos.add(LatLng(lat, lon))
                } catch (e: NumberFormatException) {
                    Log.e("DetailActivity_Debug", "Error al parsear punto: '$punto'", e)
                }
            }
        }
        return listaDePuntos
    }
}