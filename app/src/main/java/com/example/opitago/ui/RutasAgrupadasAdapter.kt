package com.example.opitago.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.opitago.R


private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_RUTA = 1

class RutasAgrupadasAdapter(
    private val onItemClicked: (RutaItem) -> Unit
) : ListAdapter<MainScreenItem, RecyclerView.ViewHolder>(MainScreenDiffCallback()) {


    inner class RutaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numeroRutaTv: TextView = itemView.findViewById(R.id.tvNumeroRuta)
        private val numeroAntiguoLayout: LinearLayout = itemView.findViewById(R.id.layout_ruta_antigua)
        private val numeroAntiguoTv: TextView = itemView.findViewById(R.id.tvNumeroAntiguo)
        private val tarjetaIdaTv: TextView = itemView.findViewById(R.id.tvTarjetaIda)
        private val tarjetaVueltaTv: TextView = itemView.findViewById(R.id.tvTarjetaVuelta)

        fun bind(rutaItem: RutaItem) {
            val rutaAgrupada = rutaItem.rutaAgrupada
            numeroRutaTv.text = rutaAgrupada.nombre

            if (!rutaAgrupada.numeroAntiguo.isNullOrEmpty()) {
                numeroAntiguoLayout.visibility = View.VISIBLE
                numeroAntiguoTv.text = rutaAgrupada.numeroAntiguo
            } else {
                numeroAntiguoLayout.visibility = View.GONE
            }

            itemView.setOnClickListener { onItemClicked(rutaItem) }
            tarjetaIdaTv.text = rutaAgrupada.rutaIda?.tarjeta ?: ""
            tarjetaVueltaTv.text = rutaAgrupada.rutaVuelta?.tarjeta ?: ""
        }
    }


    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTv: TextView = itemView.findViewById(R.id.tvHeader)
        fun bind(headerItem: HeaderItem) {

            val nombreComuna = headerItem.nombreComuna
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                .replace("_", " ")

            headerTv.text = when {
                nombreComuna.all { it.isDigit() } -> "Comuna $nombreComuna"
                else -> nombreComuna
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HeaderItem -> VIEW_TYPE_HEADER
            is RutaItem -> VIEW_TYPE_RUTA
            // El else es por seguridad, no debería ocurrir
            else -> throw IllegalArgumentException("Tipo de vista desconocido")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_comuna_header, parent, false)
                HeaderViewHolder(view)
            }
            VIEW_TYPE_RUTA -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ruta_agrupada, parent, false)
                RutaViewHolder(view)
            }
            else -> throw IllegalArgumentException("Tipo de vista desconocido")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is HeaderItem -> (holder as HeaderViewHolder).bind(item)
            is RutaItem -> (holder as RutaViewHolder).bind(item)
        }
    }
}


class MainScreenDiffCallback : DiffUtil.ItemCallback<MainScreenItem>() {
    override fun areItemsTheSame(oldItem: MainScreenItem, newItem: MainScreenItem): Boolean {
        return if (oldItem is HeaderItem && newItem is HeaderItem) {
            oldItem.nombreComuna == newItem.nombreComuna
        } else if (oldItem is RutaItem && newItem is RutaItem) {
            oldItem.rutaAgrupada.nombre == newItem.rutaAgrupada.nombre
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItem: MainScreenItem, newItem: MainScreenItem): Boolean {
        return oldItem == newItem
    }
}