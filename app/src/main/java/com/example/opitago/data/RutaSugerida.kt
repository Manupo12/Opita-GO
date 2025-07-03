package com.example.opitago.data


sealed class RutaSugerida

data class RutaSugeridaDirecta(
    val ruta: RutaAgrupada
) : RutaSugerida()

data class RutaSugeridaTransbordo(
    val rutaInicial: RutaAgrupada,
    val rutaFinal: RutaAgrupada,
    val puntoTransbordo: String
) : RutaSugerida()