package com.gcap.main.industryContacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gcap.R
import com.gcap.core.models.IndustryItem
import com.squareup.picasso.Picasso

class IndustryAdapter(
    private val IndustryList: List<IndustryItem>,
    private val onItemClick: (IndustryItem) -> Unit
) :
    RecyclerView.Adapter<IndustryAdapter.IndustryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_industry, parent, false)
        return IndustryViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val item = IndustryList[position]
        holder.bind(item)

        Picasso.get()
            .load("https://gcapcoolworks.com/" + item.image)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = IndustryList.size

    class IndustryViewHolder(itemView: View, private val onItemClick: (IndustryItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvIndustry)
        val imageView: ImageView = itemView.findViewById(R.id.ivIndustry)

        fun bind(item: IndustryItem) {
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
