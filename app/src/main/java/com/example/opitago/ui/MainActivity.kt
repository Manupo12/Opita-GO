package com.example.opitago.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    }

    private fun configurarRecyclerView() {
        // Ahora le pasamos la acción del clic al crear el adapter
        rutasAdapter = RutasAdapter { ruta ->
            // Esto se ejecutará cuando se haga clic en una ruta
            Toast.makeText(this, "Clic en: ${ruta.nombre} - ${ruta.sentido}", Toast.LENGTH_SHORT).show()
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
}