// En el archivo ui/MainScreenItem.kt
package com.example.opitago.ui

import com.example.opitago.data.RutaAgrupada

sealed interface MainScreenItem


data class HeaderItem(val nombreComuna: String) : MainScreenItem


data class RutaItem(val rutaAgrupada: RutaAgrupada) : MainScreenItem