package com.example.opitago.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.opitago.data.Ruta
import com.example.opitago.data.RutaRepository
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val repository: RutaRepository) : ViewModel() {

    // Exponemos el Flow de todas las rutas directamente desde el repositorio.
    // La UI observará este Flow para mostrar la lista.
    val todasLasRutas: Flow<List<Ruta>> = repository.todasLasRutas

}