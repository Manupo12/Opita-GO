package com.example.opitago.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.opitago.R
import com.example.opitago.data.Ruta

class RutasAdapter(private val onItemClicked: (Ruta) -> Unit) : ListAdapter<Ruta, RutasAdapter.RutaViewHolder>(RutasDiffCallback()) {

    inner class RutaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tarjetaTextView: TextView = itemView.findViewById(R.id.tvTarjeta)
        private val sentidoTextView: TextView = itemView.findViewById(R.id.tvSentido)
        private val barriosTextView: TextView = itemView.findViewById(R.id.tvBarrios)

        fun bind(ruta: Ruta) {
            tarjetaTextView.text = ruta.tarjeta
            sentidoTextView.text = ruta.sentido
            barriosTextView.text = ruta.barrios
            // Añadimos el OnClickListener a toda la fila
            itemView.setOnClickListener {
                onItemClicked(ruta)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ruta, parent, false)
        return RutaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RutaViewHolder, position: Int) {
        val ruta = getItem(position)
        holder.bind(ruta)
    }
}

class RutasDiffCallback : DiffUtil.ItemCallback<Ruta>() {
    override fun areItemsTheSame(oldItem: Ruta, newItem: Ruta): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Ruta, newItem: Ruta): Boolean {
        return oldItem == newItem
    }
}