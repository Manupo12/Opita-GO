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

    private lateinit var rutasAdapter: RutasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configurarRecyclerView()
        observarRutas()
        configurarBusqueda() // <-- La llamada importante
    }

    private fun configurarRecyclerView() {
        rutasAdapter = RutasAdapter { ruta ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_RUTA_NOMBRE", ruta.nombre)
            }
            startActivity(intent)
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRutas)
        recyclerView.adapter = rutasAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observarRutas() {
        lifecycleScope.launch {
            mainViewModel.todasLasRutas.collect { listaDeRutas ->
                rutasAdapter.submitList(listaDeRutas)
                Log.d("MainActivity", "Lista actualizada en el UI: ${listaDeRutas.size} rutas")
            }
        }
    }

    // La función que conecta la UI con el ViewModel
    private fun configurarBusqueda() {
        val etBusqueda = findViewById<EditText>(R.id.etBusqueda)

        etBusqueda.doOnTextChanged { texto, _, _, _ ->
            mainViewModel.enBusquedaCambiada(texto.toString())
        }
    }
}