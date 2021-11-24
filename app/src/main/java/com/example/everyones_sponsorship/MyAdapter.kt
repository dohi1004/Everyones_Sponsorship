package com.example.everyones_sponsorship

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.advertiser.EditActivity
import com.example.everyones_sponsorship.advertiser.ProfileDetailsActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.influencer_info.view.*

class MyAdapter(var c: Context, var applications: MutableList<Influencer>,val postId: String) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
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
        holder.who.text = application.username.toString()
        holder.rating.text = application.rating.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(c, ProfileDetailsActivity::class.java)
            intent.putExtra("name",application.username)
            intent.putExtra("rating",application.rating.toString())
            intent.putExtra("snsid",application.sns)
            intent.putExtra("info",application.INFO)
            intent.putExtra("image",application.image)
            intent.putExtra("uid",application.uid)
            intent.putExtra("postId",postId)
            c.startActivity(intent)

        }
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.influencerimage)
        val who: TextView = itemView.influencername
        val rating: TextView = itemView.ratings

    }
}