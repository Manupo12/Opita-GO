package com.example.opitago.ui

import com.example.opitago.data.RutaAgrupada


sealed class ListaPrincipalItem {

    data class EncabezadoComuna(val nombreComuna: String) : ListaPrincipalItem()


    data class RutaItem(val rutaAgrupada: RutaAgrupada) : ListaPrincipalItem()
}