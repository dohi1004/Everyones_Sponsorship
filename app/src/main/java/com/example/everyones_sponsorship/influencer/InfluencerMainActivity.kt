package com.example.everyones_sponsorship.influencer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.*
import com.example.everyones_sponsorship.chat.ChatListActivity
import com.example.everyones_sponsorship.databinding.ActivityInfluencermainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.activity_influencermain.*
import kotlinx.android.synthetic.main.fragment_mypage.*
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*


class InfluencerMainActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityInfluencermainBinding
    val posts : MutableList<Post> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfluencermainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setSupportActionBar(binding.influencertoolbar)

        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = InfluencerAdapter()

        binding.mypage.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("writeTime").addChildEventListener(object:
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                        snapshot ->
                    val post = snapshot.getValue(Post::class.java)
                    post?.let{
                        if(prevChildKey == null){
                            posts.add(it)
                            recyclerview.adapter?.notifyItemInserted(posts.size-1)
                        }else{
                            val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                            posts.add(prevIndex+1, post)
                            recyclerview.adapter?.notifyItemInserted(prevIndex+1)
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
                        recyclerview.adapter?.notifyItemChanged(prevIndex+1)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                    val post = snapshot.getValue(Post::class.java)

                    post?.let{
                        val existIndex = posts.map{it.postId}.indexOf(post.postId)
                        posts.removeAt(existIndex)
                        recyclerview.adapter?.notifyItemRemoved(existIndex)

                        if(prevChildKey == null){
                            posts.add(post)
                            recyclerview.adapter?.notifyItemChanged(posts.size-1)
                        }else{
                            val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                            posts.add(prevIndex+1,post)
                            recyclerview.adapter?.notifyItemChanged(prevIndex+1)
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
                        recyclerview.adapter?.notifyItemRemoved(existIndex)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error?.toException()?.printStackTrace()
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_influencer,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemview = item.itemId

        // for extension (search influencer) -> use when
        when(itemview){
            R.id.chat -> {
                val intent = Intent(this, ChatListActivity::class.java)
                intent.putExtra("whoami","Influencers")
                startActivity(intent)
            }
            R.id.categorymenu -> {
                val intent = Intent(this, CategoryActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }

    inner class InfluencerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff
        val who : TextView = itemView.writers

    }

    inner class InfluencerAdapter: RecyclerView.Adapter<InfluencerViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfluencerViewHolder {
            return InfluencerViewHolder(LayoutInflater.from(this@InfluencerMainActivity).inflate(R.layout.posts,parent,false))
        }

        override fun getItemCount(): Int {
            return posts.size
        }

        override fun onBindViewHolder(holder: InfluencerViewHolder, position: Int) {
            val post = posts[position]
            Picasso.get().load(Uri.parse(post.image)).fit().centerCrop().into(holder.imageView)
            holder.contentsText.text = post.productname
            holder.who.text = post.postId
            holder.timeTextView.text= getDiffTimeText(post.writeTime as Long)
            // recylcer view item 클릭 시
            holder.itemView.setOnClickListener {
                val intent = Intent(this@InfluencerMainActivity, ProductDetailsActivity::class.java)
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