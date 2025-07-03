package com.example.opitago.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opitago.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.launch
import java.util.Arrays

class PlannerActivity : AppCompatActivity() {

    private val viewModel: PlannerViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var resultadosAdapter: ResultadosPlannerAdapter
    private lateinit var autocompleteFragmentOrigen: AutocompleteSupportFragment

    private var origenSeleccionado: Place? = null
    private var destinoSeleccionado: Place? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                obtenerUbicacionActual()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planner)

        inicializarPlaces()
        viewModel.init(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        configurarToolbar()
        configurarAutocompletadoOrigen()
        configurarAutocompletadoDestino()
        configurarBotonBuscar()
        configurarRecyclerView()
        observarEstadoPlanner()
        configurarBotonMiUbicacion()
    }

    private fun inicializarPlaces() {
        if (!Places.isInitialized()) {
            try {
                val apiKey = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    .metaData.getString("com.google.android.geo.API_KEY")
                if (apiKey != null) {
                    Places.initialize(applicationContext, apiKey)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun configurarAutocompletadoOrigen() {
        autocompleteFragmentOrigen =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_origen)
                    as AutocompleteSupportFragment

        autocompleteFragmentOrigen.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
        autocompleteFragmentOrigen.setHint("Punto de Origen")
        autocompleteFragmentOrigen.setCountry("CO")


        val neivaBounds = RectangularBounds.newInstance(
            com.google.android.gms.maps.model.LatLng(2.89, -75.34), // Coordenada Suroeste de Neiva
            com.google.android.gms.maps.model.LatLng(3.00, -75.23)  // Coordenada Noreste de Neiva
        )
        autocompleteFragmentOrigen.setLocationBias(neivaBounds)


        autocompleteFragmentOrigen.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                origenSeleccionado = place
            }
            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("PlannerActivity", "Error en autocompletado de origen: $status")
            }
        })
    }

    private fun configurarAutocompletadoDestino() {
        val autocompleteFragmentDestino =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_destino)
                    as AutocompleteSupportFragment

        autocompleteFragmentDestino.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragmentDestino.setHint("Punto de Destino")
        autocompleteFragmentDestino.setCountry("CO")


        val neivaBounds = RectangularBounds.newInstance(
            com.google.android.gms.maps.model.LatLng(2.89, -75.34),
            com.google.android.gms.maps.model.LatLng(3.00, -75.23)
        )
        autocompleteFragmentDestino.setLocationBias(neivaBounds)


        autocompleteFragmentDestino.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                destinoSeleccionado = place
            }
            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("PlannerActivity", "Error en autocompletado de destino: $status")
            }
        })
    }

    private fun configurarBotonMiUbicacion() {
        val btnMyLocation = findViewById<ImageButton>(R.id.btn_get_current_location)
        btnMyLocation.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    obtenerUbicacionActual()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUbicacionActual() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    reverseGeocode(location)
                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación. Activa el GPS.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun reverseGeocode(location: Location) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses?.firstOrNull()
            val addressText = address?.getAddressLine(0) ?: "${location.latitude}, ${location.longitude}"

            autocompleteFragmentOrigen.setText(addressText)
            origenSeleccionado = Place.builder()
                .setLatLng(com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude))
                .setName(addressText)
                .setAddress(addressText)
                .build()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al obtener la dirección.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarBotonBuscar() {
        val btnBuscar = findViewById<Button>(R.id.btn_buscar_viaje)
        btnBuscar.setOnClickListener {
            if (origenSeleccionado != null && destinoSeleccionado != null && origenSeleccionado?.latLng != null && destinoSeleccionado?.latLng != null) {
                viewModel.calcularMejorRuta(origenSeleccionado!!.latLng!!, destinoSeleccionado!!.latLng!!)
            } else {
                Toast.makeText(this, "Por favor, selecciona un origen y un destino.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_planner)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configurarRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_resultados)
        resultadosAdapter = ResultadosPlannerAdapter(emptyList()) { nombreRuta ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_RUTA_NOMBRE", nombreRuta)
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = resultadosAdapter
    }

    private fun observarEstadoPlanner() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar_planner)
        val btnBuscar = findViewById<Button>(R.id.btn_buscar_viaje)

        lifecycleScope.launch {
            viewModel.plannerState.collect { state ->
                when (state) {
                    is PlannerState.Idle -> {
                        progressBar.visibility = View.GONE
                        btnBuscar.visibility = View.VISIBLE
                    }
                    is PlannerState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnBuscar.visibility = View.GONE
                    }
                    is PlannerState.Success -> {
                        progressBar.visibility = View.GONE
                        btnBuscar.visibility = View.VISIBLE
                        resultadosAdapter.actualizarResultados(state.resultados)
                        if (state.resultados.isEmpty()) {
                            Toast.makeText(this@PlannerActivity, "No se encontraron rutas para este viaje.", Toast.LENGTH_LONG).show()
                        }
                    }
                    is PlannerState.Error -> {
                        progressBar.visibility = View.GONE
                        btnBuscar.visibility = View.VISIBLE
                        Toast.makeText(this@PlannerActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}