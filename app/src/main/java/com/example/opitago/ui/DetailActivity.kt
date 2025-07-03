package com.example.opitago.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val detailViewModel: DetailViewModel by viewModels {
        val database = OpitaGODatabase.getInstance(applicationContext)
        val repository = RutaRepository(database.rutaDao())
        DetailViewModelFactory(repository)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                activarMiUbicacion()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val nombreRuta = intent.getStringExtra("EXTRA_RUTA_NOMBRE")
        if (nombreRuta != null) {
            detailViewModel.cargarRutasPorNombre(nombreRuta)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab_proximidad)
        fab.setOnClickListener {
            if (::map.isInitialized && map.isMyLocationEnabled && map.myLocation != null) {
                calcularYMostrarPuntoMasCercano(map.myLocation)
            } else {
                Toast.makeText(this, "Ubicación no disponible. Activa el GPS e inténtalo de nuevo.", Toast.LENGTH_LONG).show()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        observarRutas()
        habilitarCapaDeUbicacion()
    }

    private fun habilitarCapaDeUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            activarMiUbicacion()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun activarMiUbicacion() {
        try {
            map.isMyLocationEnabled = true
        } catch (e: SecurityException) {
            Log.e("DetailActivity", "Error de seguridad al activar la ubicación.", e)
        }
    }

    private fun calcularYMostrarPuntoMasCercano(miUbicacion: Location) {
        val rutasActuales = detailViewModel.rutas.value
        if (rutasActuales.isEmpty()) return

        var puntoMasCercano: LatLng? = null
        var distanciaMinima = Float.MAX_VALUE

        rutasActuales.forEach { ruta ->
            val puntosDeRuta = parseCoordenadas(ruta.coordenadas)
            puntosDeRuta.forEach { punto ->
                val distancia = FloatArray(1)
                Location.distanceBetween(
                    miUbicacion.latitude, miUbicacion.longitude,
                    punto.latitude, punto.longitude,
                    distancia
                )
                if (distancia[0] < distanciaMinima) {
                    distanciaMinima = distancia[0]
                    puntoMasCercano = punto
                }
            }
        }

        puntoMasCercano?.let { puntoSeguro ->
            map.addMarker(MarkerOptions().position(puntoSeguro).title("Punto más cercano"))
            Toast.makeText(
                this,
                "El punto más cercano de la ruta está a ${distanciaMinima.toInt()} metros.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun observarRutas() {
        lifecycleScope.launch {
            detailViewModel.rutas.collect { listaDeRutas ->
                if (listaDeRutas.isNotEmpty()) {
                    dibujarRutasEnMapa(listaDeRutas)
                    mostrarDescripciones(listaDeRutas)
                }
            }
        }
    }

    private fun dibujarRutasEnMapa(rutas: List<Ruta>) {
        map.clear()
        val boundsBuilder = LatLngBounds.Builder()
        rutas.forEach { ruta ->
            val puntosDeRuta = parseCoordenadas(ruta.coordenadas)
            if (puntosDeRuta.isNotEmpty()) {
                val colorDeRuta = if (ruta.sentido == "Ida") ContextCompat.getColor(this, R.color.opita_yellow_primary) else ContextCompat.getColor(this, R.color.opita_red_accent)
                val polylineOptions = PolylineOptions()
                    .addAll(puntosDeRuta)
                    .color(colorDeRuta)
                    .width(12f)
                map.addPolyline(polylineOptions)
                puntosDeRuta.forEach { punto ->
                    boundsBuilder.include(punto)
                }
            }
        }
        try {
            val bounds = boundsBuilder.build()
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
        } catch (e: IllegalStateException) {
            val neiva = LatLng(2.9273, -75.2819)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(neiva, 13f))
            Log.e("DetailActivity", "No se pudo construir los límites para el zoom.", e)
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

    private fun mostrarDescripciones(rutas: List<Ruta>) {
        val headerTv = findViewById<TextView>(R.id.tvDetailHeader)
        val recorridoIdaTv = findViewById<TextView>(R.id.tvRecorridoIda)
        val recorridoVueltaTv = findViewById<TextView>(R.id.tvRecorridoVuelta)

        val nombreRuta = rutas.firstOrNull()?.nombre ?: "Ruta"
        headerTv.text = "Ruta $nombreRuta"

        val rutaIda = rutas.find { it.sentido == "Ida" || it.sentido == "Único" }
        val rutaVuelta = rutas.find { it.sentido == "Vuelta" }

        recorridoIdaTv.text = rutaIda?.recorrido ?: "No disponible"
        recorridoVueltaTv.text = rutaVuelta?.recorrido ?: "No disponible"
    }
}