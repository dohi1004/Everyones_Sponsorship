package com.example.everyones_sponsorship

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.influencer_info.view.*

class MyAdapter(var c: Context, var applications:MutableList<Application>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.influencer_info, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return applications.size
    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
        val application = applications[position]
        Picasso.get().load(Uri.parse(application.image)).fit().centerCrop().into(holder.imageView)
        holder.who.text = application.influencerId.toString()
        holder.rating.text = application.rating.toString()
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.productimage)
        val who: TextView = itemView.influencername
        val rating: TextView = itemView.ratings

    }
}