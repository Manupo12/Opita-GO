package com.example.opitago.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opitago.data.Ruta
import com.example.opitago.data.RutaAgrupada
import com.example.opitago.data.RutaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(private val repository: RutaRepository) : ViewModel() {

    private val _terminoDeBusqueda = MutableStateFlow("")
    val terminoDeBusqueda: StateFlow<String> = _terminoDeBusqueda

    fun enBusquedaCambiada(query: String) {
        _terminoDeBusqueda.value = query
    }

    val rutasAgrupadas: StateFlow<List<RutaAgrupada>> = _terminoDeBusqueda
        .flatMapLatest { query ->
            val flowDeRutas = if (query.isBlank()) {
                repository.todasLasRutas
            } else {
                repository.buscarRutas("%$query%")
            }

            flowDeRutas.map { listaPlana ->
                listaPlana.groupBy { it.nombre }
                    .map { (nombre, rutasDelGrupo) ->
                        RutaAgrupada(
                            nombre = nombre,
                            numeroAntiguo = rutasDelGrupo.firstNotNullOfOrNull { it.numeroAntiguo },
                            rutaIda = rutasDelGrupo.find { it.sentido == "Ida" || it.sentido == "Único" },
                            rutaVuelta = rutasDelGrupo.find { it.sentido == "Vuelta" }
                        )
                    }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}