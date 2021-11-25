package com.example.everyones_sponsorship.influencer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.Post
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.databinding.ActivityCategorydetailsBinding
import com.example.everyones_sponsorship.databinding.ActivitySearchresultBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_categorydetails.*
import kotlinx.android.synthetic.main.activity_searchresult.*
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*

class SearchresultActivity : AppCompatActivity() {
    val posts : MutableList<Post> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivitySearchresultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val searchname = getIntent().getStringExtra("text")
        val resulttext = "\"$searchname\""
        binding.resulttext.text = resulttext

        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        serachrecyclerview.layoutManager = layoutManager
        serachrecyclerview.adapter = InfluencerAdapter()

        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("productname").equalTo("$searchname").addChildEventListener(object:
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                        snapshot ->
                    val post = snapshot.getValue(Post::class.java)
                    post?.let{
                        if(prevChildKey == null){
                            posts.add(it)
                            serachrecyclerview.adapter?.notifyItemInserted(posts.size-1)
                        }else{
                            val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                            posts.add(prevIndex+1, post)
                            serachrecyclerview.adapter?.notifyItemInserted(prevIndex+1)
                        }

                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                    val post = snapshot.getValue(Post::class.java)
                    post?.let{
                        val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                        posts[prevIndex + 1] = post
                        serachrecyclerview.adapter?.notifyItemChanged(prevIndex+1)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                    val post = snapshot.getValue(Post::class.java)

                    post?.let{
                        val existIndex = posts.map{it.postId}.indexOf(post.postId)
                        posts.removeAt(existIndex)
                        serachrecyclerview.adapter?.notifyItemRemoved(existIndex)

                        if(prevChildKey == null){
                            posts.add(post)
                            serachrecyclerview.adapter?.notifyItemChanged(posts.size-1)
                        }else{
                            val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                            posts.add(prevIndex+1,post)
                            serachrecyclerview.adapter?.notifyItemChanged(prevIndex+1)
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot?.let{
                    val post = snapshot.getValue(Post::class.java)
                    post?.let{post->
                        val existIndex = posts.map{it.postId}.indexOf(post.postId)
                        posts.removeAt(existIndex)
                        serachrecyclerview.adapter?.notifyItemRemoved(existIndex)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error?.toException()?.printStackTrace()
            }
        })

    }

    inner class InfluencerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff

    }

    inner class InfluencerAdapter: RecyclerView.Adapter<InfluencerViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfluencerViewHolder {
            return InfluencerViewHolder(LayoutInflater.from(this@SearchresultActivity).inflate(R.layout.posts,parent,false))
        }

        override fun getItemCount(): Int {
            return posts.size
        }

        override fun onBindViewHolder(holder: InfluencerViewHolder, position: Int) {
            val post = posts[position]
            Picasso.get().load(Uri.parse(post.image)).fit().centerCrop().into(holder.imageView)
            holder.contentsText.text = post.productname
            holder.timeTextView.text= getDiffTimeText(post.writeTime as Long)
            // recylcer view item 클릭 시
            holder.itemView.setOnClickListener {
                intent.putExtra("postId",post.postId)
                val intent = Intent(this@SearchresultActivity, ProductDetailsActivity::class.java)
                intent.putExtra("postId",post.postId)
                intent.putExtra("productname",post.productname)
                intent.putExtra("message",post.message)
                intent.putExtra("category",post.category)
                intent.putExtra("rating",post.rating)
                intent.putExtra("image",post.image)
                intent.putExtra("timestamp", post.writeTime as Long)
                startActivity(intent)
            }
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
                val format = SimpleDateFormat("yyyy.MM.DD.HH")
                return format.format(Date(targetTime))
            }

        }


    }


}