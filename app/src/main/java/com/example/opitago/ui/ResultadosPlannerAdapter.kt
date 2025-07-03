package com.example.opitago.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.opitago.R
import com.example.opitago.data.RutaAgrupada
import com.example.opitago.data.RutaSugerida
import com.example.opitago.data.RutaSugeridaDirecta
import com.example.opitago.data.RutaSugeridaTransbordo

class ResultadosPlannerAdapter(
    private var resultados: List<RutaSugerida>,
    private val onRouteClicked: (String) -> Unit
) : RecyclerView.Adapter<ResultadosPlannerAdapter.ResultadoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_resultado_planner, parent, false)
        return ResultadoViewHolder(view)
    }

    override fun getItemCount(): Int = resultados.size

    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        holder.bind(resultados[position])
    }

    fun actualizarResultados(nuevosResultados: List<RutaSugerida>) {
        this.resultados = nuevosResultados
        notifyDataSetChanged()
    }

    inner class ResultadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tipoViajeTv: TextView = itemView.findViewById(R.id.tv_tipo_viaje)
        private val transferIcon: ImageView = itemView.findViewById(R.id.iv_transfer_icon)


        private val containerRuta1: View = itemView.findViewById(R.id.container_ruta_1)
        private val containerRuta2: View = itemView.findViewById(R.id.container_ruta_2)


        private val numeroActualTv1: TextView = itemView.findViewById(R.id.tv_pill_1_numero_actual)
        private val layoutAntiguo1: View = itemView.findViewById(R.id.layout_pill_1_numero_antiguo)
        private val numeroAntiguoTv1: TextView = itemView.findViewById(R.id.tv_pill_1_numero_antiguo)


        private val numeroActualTv2: TextView = itemView.findViewById(R.id.tv_pill_2_numero_actual)
        private val layoutAntiguo2: View = itemView.findViewById(R.id.layout_pill_2_numero_antiguo)

        private val numeroAntiguoTv2: TextView = itemView.findViewById(R.id.tv_pill_2_numero_antiguo)


        fun bind(resultado: RutaSugerida) {
            when (resultado) {
                is RutaSugeridaDirecta -> {
                    tipoViajeTv.text = "Ruta Directa"


                    numeroActualTv1.text = resultado.ruta.nombre
                    if (!resultado.ruta.numeroAntiguo.isNullOrEmpty()) {
                        layoutAntiguo1.visibility = View.VISIBLE
                        numeroAntiguoTv1.text = resultado.ruta.numeroAntiguo
                    } else {
                        layoutAntiguo1.visibility = View.GONE
                    }


                    containerRuta2.visibility = View.GONE
                    transferIcon.visibility = View.GONE


                    containerRuta1.setOnClickListener { onRouteClicked(resultado.ruta.nombre) }
                }
                is RutaSugeridaTransbordo -> {
                    tipoViajeTv.text = "Ruta con Transbordo"

                    containerRuta2.visibility = View.VISIBLE
                    transferIcon.visibility = View.VISIBLE


                    numeroActualTv1.text = resultado.rutaInicial.nombre
                    if (!resultado.rutaInicial.numeroAntiguo.isNullOrEmpty()) {
                        layoutAntiguo1.visibility = View.VISIBLE
                        numeroAntiguoTv1.text = resultado.rutaInicial.numeroAntiguo
                    } else {
                        layoutAntiguo1.visibility = View.GONE
                    }


                    numeroActualTv2.text = resultado.rutaFinal.nombre
                    if (!resultado.rutaFinal.numeroAntiguo.isNullOrEmpty()) {
                        layoutAntiguo2.visibility = View.VISIBLE
                        numeroAntiguoTv2.text = resultado.rutaFinal.numeroAntiguo
                    } else {
                        layoutAntiguo2.visibility = View.GONE
                    }


                    containerRuta1.setOnClickListener { onRouteClicked(resultado.rutaInicial.nombre) }
                    containerRuta2.setOnClickListener { onRouteClicked(resultado.rutaFinal.nombre) }
                }
            }
        }
    }
}