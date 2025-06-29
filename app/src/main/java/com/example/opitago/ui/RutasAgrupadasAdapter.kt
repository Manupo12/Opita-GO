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
import com.example.opitago.data.RutaAgrupada

class RutasAgrupadasAdapter(
    private val onItemClicked: (RutaAgrupada) -> Unit
) : ListAdapter<RutaAgrupada, RutasAgrupadasAdapter.RutaAgrupadaViewHolder>(RutaAgrupadaDiffCallback()) {

    inner class RutaAgrupadaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val numeroRutaTv: TextView = itemView.findViewById(R.id.tvNumeroRuta)
        private val numeroAntiguoLayout: LinearLayout = itemView.findViewById(R.id.layout_ruta_antigua)
        private val numeroAntiguoTv: TextView = itemView.findViewById(R.id.tvNumeroAntiguo)


        private val layoutColumnas: LinearLayout = itemView.findViewById(R.id.layout_columnas)
        private val tarjetaIdaTv: TextView? = itemView.findViewById(R.id.tvTarjetaIda)
        private val tarjetaVueltaTv: TextView? = itemView.findViewById(R.id.tvTarjetaVuelta)

        fun bind(rutaAgrupada: RutaAgrupada) {

            numeroRutaTv.text = rutaAgrupada.nombre

            if (!rutaAgrupada.numeroAntiguo.isNullOrEmpty()) {
                numeroAntiguoLayout.visibility = View.VISIBLE
                numeroAntiguoTv.text = rutaAgrupada.numeroAntiguo
            } else {
                numeroAntiguoLayout.visibility = View.GONE
            }


            layoutColumnas.setOnClickListener { onItemClicked(rutaAgrupada) }


            tarjetaIdaTv?.text = rutaAgrupada.rutaIda?.tarjeta ?: ""
            tarjetaVueltaTv?.text = rutaAgrupada.rutaVuelta?.tarjeta ?: ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutaAgrupadaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ruta_agrupada, parent, false)
        return RutaAgrupadaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RutaAgrupadaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class RutaAgrupadaDiffCallback : DiffUtil.ItemCallback<RutaAgrupada>() {
    override fun areItemsTheSame(oldItem: RutaAgrupada, newItem: RutaAgrupada): Boolean {
        return oldItem.nombre == newItem.nombre
    }

    override fun areContentsTheSame(oldItem: RutaAgrupada, newItem: RutaAgrupada): Boolean {
        return oldItem == newItem
    }
}