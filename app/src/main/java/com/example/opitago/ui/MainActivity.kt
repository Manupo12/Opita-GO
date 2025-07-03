package com.example.opitago.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opitago.R
import com.example.opitago.data.OpitaGODatabase
import com.example.opitago.data.RutaRepository
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        val database = OpitaGODatabase.getInstance(applicationContext)
        val repository = RutaRepository(database.rutaDao())
        MainViewModelFactory(repository)
    }

    private lateinit var rutasAdapter: RutasAgrupadasAdapter


    private lateinit var filtroChips: Map<TextView, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        inicializarFiltros()

        configurarRecyclerView()
        configurarBusqueda()
        configurarFiltrosRapidos()
        observarRutas()
        observarFiltroActivo()
        configurarBotonDeAyuda()
        configurarBotonPlanificador()
        // configurarColorDinamicoDelAppBar()
    }

    private fun inicializarFiltros() {

        filtroChips = mapOf(
            findViewById<TextView>(R.id.chip_centros_comerciales) to "Centro Comercial",
            findViewById<TextView>(R.id.chip_universidades) to "Universidad",
            findViewById<TextView>(R.id.chip_hospitales) to "Hospital",
            findViewById<TextView>(R.id.chip_comuna_1) to "1",
            findViewById<TextView>(R.id.chip_comuna_2) to "2",
            findViewById<TextView>(R.id.chip_comuna_3) to "3",
            findViewById<TextView>(R.id.chip_caguan) to "Corregimiento El Caguan",
            findViewById<TextView>(R.id.chip_fortalecillas) to "Corregimiento Fortalecillas"

        )
    }


    private fun configurarFiltrosRapidos() {
        val etBusqueda = findViewById<TextInputEditText>(R.id.etBusqueda)

        filtroChips.forEach { (chipView, filtroValue) ->
            chipView.setOnClickListener {
                mainViewModel.setFiltroActivo(filtroValue)
                etBusqueda.text?.clear()
            }
        }
    }


    private fun observarFiltroActivo() {
        lifecycleScope.launch {
            mainViewModel.filtroActivo.collect { filtroActivo ->
                filtroChips.forEach { (chipView, filtroValue) ->
                    val isSelected = filtroValue == filtroActivo
                    if (isSelected) {
                        chipView.setBackgroundResource(R.drawable.background_filter_chip_active)
                        chipView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.opita_yellow_primary))
                    } else {
                        chipView.setBackgroundResource(R.drawable.background_filter_chip)
                        chipView.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    }
                }
            }
        }
    }



    private fun configurarBotonDeAyuda() {
        val helpButton = findViewById<ImageButton>(R.id.btn_ayuda)
        helpButton.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info_rutas, null)
            MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Entendido") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun configurarRecyclerView() {
        rutasAdapter = RutasAgrupadasAdapter { rutaItem ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_RUTA_NOMBRE", rutaItem.rutaAgrupada.nombre)
            }
            startActivity(intent)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRutas)
        recyclerView.adapter = rutasAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observarRutas() {
        lifecycleScope.launch {
            mainViewModel.rutasVisibles.collect { listaDeItems ->
                rutasAdapter.submitList(listaDeItems)
            }
        }
    }

    private fun configurarBusqueda() {
        val etBusqueda = findViewById<TextInputEditText>(R.id.etBusqueda)
        etBusqueda.doOnTextChanged { texto, _, _, _ ->
            mainViewModel.enBusquedaCambiada(texto.toString())
        }
    }
    private fun configurarBotonPlanificador() {
        val fab = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_planificador)
        fab.setOnClickListener {
            val intent = Intent(this, PlannerActivity::class.java)
            startActivity(intent)
        }
    }
}