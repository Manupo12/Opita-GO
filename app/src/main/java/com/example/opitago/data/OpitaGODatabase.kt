package com.example.opitago.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Ruta::class], version = 3, exportSchema = false)
abstract class OpitaGODatabase : RoomDatabase() {

    abstract fun rutaDao(): RutaDao

    companion object {
        @Volatile
        private var INSTANCE: OpitaGODatabase? = null

        fun getInstance(context: Context): OpitaGODatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OpitaGODatabase::class.java,
                    "opitago_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(databaseCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val databaseCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Este código se ejecuta UNA SOLA VEZ cuando la DB se crea.
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val rutaDao = database.rutaDao()
                        // Insertar datos reales de la Ruta 247
                        rutaDao.insertar(Ruta(
                            nombre = "247",
                            tarjeta = "247 Caña Brava",
                            sentido = "Ida",
                            horario = "Hasta 7:30pm",
                            recorrido = "CRA 2, Comuneros, Cándido, Usco, Gaitán, Las Américas, Los Parques",
                            barrios = "Comuneros, Cándido, Usco, Gaitán, Las Américas, Los Parques, Sur Orientales, Las Brisas, Claretiano"
                        ))
                        rutaDao.insertar(Ruta(
                            nombre = "247",
                            tarjeta = "247 Los Alpes",
                            sentido = "Vuelta",
                            horario = "Hasta 7:30pm",
                            recorrido = "CRA 7, CRA 5, Comfamiliar, Usco, Cándido 2, C.C. Único, Homecenter",
                            barrios = "Comfamiliar, Usco, Cándido, Único, Homecenter, Los Pinos, Caña Brava, Sur Orientales, Los Alpes"
                        ))
                    }
                }
            }
        }
    }
}