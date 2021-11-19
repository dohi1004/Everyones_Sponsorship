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
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*

open class ListAdapter(var c: Context, var posts:MutableList<Post>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.posts,parent,false))
    }
    override fun getItemCount(): Int {
        return posts.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff
        val who : TextView = itemView.writers

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        Picasso.get().load(Uri.parse(post.image)).fit().centerCrop().into(holder.imageView)
        holder.contentsText.text = post.productname
        holder.who.text = post.postId
        holder.timeTextView.text= getDiffTimeText(post.writeTime as Long)
    }
    fun getDiffTimeText(targetTime: Long) : String{
        val curDateTime = DateTime()
        val targetDateTime = DateTime().withMillis(targetTime)
        val diffDay = Days.daysBetween(curDateTime, targetDateTime).days
        val diffHours = Hours.hoursBetween(targetDateTime, curDateTime).hours
        val diffMinutes = Minutes.minutesBetween(targetDateTime, curDateTime).minutes

        if(diffDay == 0){
            if(diffHours == 0 && diffMinutes == 0){
                return "Just before"
            }
            return if (diffHours > 0){
                if(diffHours == 1){
                    " "+ diffHours + "hour ago"
                }
                else{"" + diffHours + "hours ago"}
            }else{
                if(diffMinutes == 1) {
                    ""+diffMinutes+"minutes ago"
                }
                else{
                    ""+diffMinutes+"minutes ago"
                }
            }
        }else{
            val format = SimpleDateFormat("yyyy-MM-DD")
            return format.format(Date(targetTime))
        }

    }
}