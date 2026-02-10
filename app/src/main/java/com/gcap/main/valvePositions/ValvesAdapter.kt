package com.gcap.main.valvePositions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gcap.R
import com.gcap.core.models.ValveItem
import com.squareup.picasso.Picasso

class ValvesAdapter(
    private val valveList: List<ValveItem>,
    private val onItemClick: (ValveItem) -> Unit
) :
    RecyclerView.Adapter<ValvesAdapter.ValveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_brand, parent, false)
        return ValveViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ValveViewHolder, position: Int) {
        val item = valveList[position]
        holder.bind(item)

        Picasso.get()
            .load("https://gcapcoolworks.com/" + item.image)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = valveList.size

    class ValveViewHolder(itemView: View, private val onItemClick: (ValveItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvName)
        val imageView: ImageView = itemView.findViewById(R.id.ivBrand)

        fun bind(item: ValveItem) {
            nameTextView.text = item.name
            Picasso.get()
                .load("https://gcapcoolworks.com/" + item.image)
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
