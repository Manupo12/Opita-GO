package com.example.opitago.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opitago.data.Ruta
import com.example.opitago.data.RutaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val repository: RutaRepository) : ViewModel() {

    private val _terminoDeBusqueda = MutableStateFlow("")
    val terminoDeBusqueda: StateFlow<String> = _terminoDeBusqueda

    fun enBusquedaCambiada(query: String) {
        _terminoDeBusqueda.value = query
    }

    val todasLasRutas: StateFlow<List<Ruta>> = _terminoDeBusqueda
        .flatMapLatest { query ->
            if (query.isBlank()) {
                // CAMBIO: Accedemos a la propiedad, sin paréntesis
                repository.todasLasRutas
            } else {
                repository.buscarRutas("%$query%")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}