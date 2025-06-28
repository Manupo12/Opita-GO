package com.example.opitago.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_de_rutas")
data class Ruta(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre_ruta")
    val nombre: String, // Ej: "247"

    @ColumnInfo(name = "tarjeta_visual") // <-- NUEVA COLUMNA
    val tarjeta: String, // Ej: "247 Caña Brava"

    @ColumnInfo(name = "sentido_ruta")
    val sentido: String, // Ej: "Ida" o "Vuelta"

    @ColumnInfo(name = "horario_ruta")
    val horario: String,

    @ColumnInfo(name = "puntos_clave_recorrido")
    val recorrido: String,

    @ColumnInfo(name = "barrios_principales")
    val barrios: String
)