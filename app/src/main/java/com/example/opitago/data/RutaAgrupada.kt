package com.example.opitago.data


data class RutaAgrupada(
    val nombre: String,
    val numeroAntiguo: String?,
    val rutaIda: Ruta?,
    val rutaVuelta: Ruta?,
    val isExpanded: Boolean
)