package com.example.opitago.data

// Esta clase no es una tabla de la base de datos, es solo un "contenedor"
// para mostrar los datos en la UI de forma agrupada.
data class RutaAgrupada(
    val nombre: String,
    val rutaIda: Ruta?,    // La ruta de ida (puede ser nula)
    val rutaVuelta: Ruta? // La ruta de vuelta (puede ser nula)
)