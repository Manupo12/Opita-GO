package com.example.opitago.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tabla_de_rutas")
data class Ruta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "nombre_ruta")
    val nombre: String,

    @ColumnInfo(name = "tarjeta_visual")
    val tarjeta: String,

    @ColumnInfo(name = "sentido_ruta")
    val sentido: String,

    @ColumnInfo(name = "horario_ruta")
    val horario: String,

    @ColumnInfo(name = "puntos_clave_recorrido")
    val recorrido: String,

    @ColumnInfo(name = "barrios_principales")
    val barrios: String,

    @ColumnInfo(name = "coordenadas_ruta")
    val coordenadas: String,

   /* @ColumnInfo(name = "nombre_imagen")
    val nombreImagen: String,*/

    @ColumnInfo(name = "numero_antiguo")
    val numeroAntiguo: String?
)