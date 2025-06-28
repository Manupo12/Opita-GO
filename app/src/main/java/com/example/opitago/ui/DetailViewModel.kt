package com.example.opitago.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opitago.data.Ruta
import com.example.opitago.data.RutaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: RutaRepository) : ViewModel() {

    private val _rutas = MutableStateFlow<List<Ruta>>(emptyList())
    val rutas: StateFlow<List<Ruta>> = _rutas

    fun cargarRutasPorNombre(nombreRuta: String) {
        viewModelScope.launch {
            repository.obtenerRutasPorNombre(nombreRuta)
                .catch { e ->
                    // Manejar error si es necesario
                }
                .collect { listaDeRutas ->
                    _rutas.value = listaDeRutas
                }
        }
    }
}