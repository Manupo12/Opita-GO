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

    @Query("SELECT * FROM tabla_de_rutas WHERE " +
            "barrios_principales LIKE :terminoDeBusqueda COLLATE NOCASE OR " +
            "nombre_ruta LIKE :terminoDeBusqueda COLLATE NOCASE OR " +
            "puntos_clave_recorrido LIKE :terminoDeBusqueda COLLATE NOCASE OR " +
            "comuna LIKE :terminoDeBusqueda COLLATE NOCASE") // <-- LÍNEA AÑADIDA
    fun buscarRutas(terminoDeBusqueda: String): Flow<List<Ruta>>

    @Query("SELECT * FROM tabla_de_rutas WHERE nombre_ruta = :nombreRuta")
    fun obtenerRutasPorNombre(nombreRuta: String): Flow<List<Ruta>>

    @Query("SELECT * FROM tabla_de_rutas")
    suspend fun obtenerTodasLasRutasSync(): List<Ruta>

}