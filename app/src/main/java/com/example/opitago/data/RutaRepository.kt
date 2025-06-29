package com.example.opitago.data

import kotlinx.coroutines.flow.Flow

class RutaRepository(private val rutaDao: RutaDao) {

    val todasLasRutas: Flow<List<Ruta>> = rutaDao.obtenerTodasLasRutas()

    fun buscarRutas(termino: String): Flow<List<Ruta>> {
        return rutaDao.buscarRutas(termino)
    }

    suspend fun insertar(ruta: Ruta) {
        rutaDao.insertar(ruta)
    }

    fun obtenerRutasPorNombre(nombreRuta: String): Flow<List<Ruta>> {
        return rutaDao.obtenerRutasPorNombre(nombreRuta)
    }
}