package com.example.opitago.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opitago.R
import com.example.opitago.data.OpitaGODatabase
import com.example.opitago.data.RutaRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels {
        val database = OpitaGODatabase.getInstance(applicationContext)
        val repository = RutaRepository(database.rutaDao())
        MainViewModelFactory(repository)
    }

    private lateinit var rutasAdapter: RutasAgrupadasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configurarRecyclerView()
        observarRutas()
        configurarBusqueda()
    }

    private fun configurarRecyclerView() {
        // CAMBIO: La creación del adapter ahora es más simple
        rutasAdapter = RutasAgrupadasAdapter { rutaAgrupada ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_RUTA_NOMBRE", rutaAgrupada.nombre)
            }
            startActivity(intent)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRutas)
        recyclerView.adapter = rutasAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observarRutas() {
        lifecycleScope.launch {
            mainViewModel.rutasAgrupadas.collect { listaDeRutasAgrupadas ->
                rutasAdapter.submitList(listaDeRutasAgrupadas)
                Log.d("MainActivity", "Lista actualizada en el UI: ${listaDeRutasAgrupadas.size} rutas")
            }
        }
    }

    private fun configurarBusqueda() {
        val etBusqueda = findViewById<EditText>(R.id.etBusqueda)

        etBusqueda.doOnTextChanged { texto, _, _, _ ->
            mainViewModel.enBusquedaCambiada(texto.toString())
        }
    }
}