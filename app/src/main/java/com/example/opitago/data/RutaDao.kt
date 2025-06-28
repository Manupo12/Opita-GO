package com.example.opitago.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RutaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ruta: Ruta)

    @Query("SELECT * FROM tabla_de_rutas ORDER BY nombre_ruta ASC")
    fun obtenerTodasLasRutas(): Flow<List<Ruta>>

    @Query("SELECT * FROM tabla_de_rutas WHERE barrios_principales LIKE :terminoDeBusqueda OR nombre_ruta LIKE :terminoDeBusqueda")
    fun buscarRutas(terminoDeBusqueda: String): Flow<List<Ruta>>

    // NUEVA FUNCIÓN AÑADIDA
    @Query("SELECT * FROM tabla_de_rutas WHERE nombre_ruta = :nombreRuta")
    fun obtenerRutasPorNombre(nombreRuta: String): Flow<List<Ruta>>
}