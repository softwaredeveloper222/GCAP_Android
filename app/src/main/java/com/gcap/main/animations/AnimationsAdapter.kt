package com.gcap.main.animations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gcap.R
import com.gcap.core.models.AnimationItem
import androidx.core.net.toUri
import com.bumptech.glide.Glide

class AnimationsAdapter(
    private val AnimationsList: List<AnimationItem>,
    private val onItemClick: (AnimationItem) -> Unit
) :
    RecyclerView.Adapter<AnimationsAdapter.AnimationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimationsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animation, parent, false)
        return AnimationsViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: AnimationsViewHolder, position: Int) {
        val item = AnimationsList[position]
        holder.bind(item)

        val uri = ("https://gcapcoolworks.com/" + item.image).toUri()

        Glide.with(holder.imageView.context).asBitmap()
            .load(uri)
            .frame(1_000_000)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int = AnimationsList.size

    class AnimationsViewHolder(itemView: View, private val onItemClick: (AnimationItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvAnimation)
        val imageView: ImageView = itemView.findViewById(R.id.ivAnimation)

        fun bind(item: AnimationItem) {
            nameTextView.text = item.name
            val uri = ("https://gcapcoolworks.com/" + item.image).toUri()

            Glide.with(imageView.context).asBitmap()
                .load(uri)
                .frame(1_000_000)
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}
