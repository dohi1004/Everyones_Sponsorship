package com.example.everyones_sponsorship.influencer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.everyones_sponsorship.*
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.advertiser.AdvertiserApplicationActivity
import com.example.everyones_sponsorship.advertiser.EditActivity
import com.example.everyones_sponsorship.databinding.ActivityAdvertiserMainBinding
import com.example.everyones_sponsorship.databinding.ActivityMypageBinding
import com.example.everyones_sponsorship.databinding.ActivityProductinfoBinding
import com.example.everyones_sponsorship.start.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.activity_applicationlist.*
import kotlinx.android.synthetic.main.activity_categorydetails.*
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*
class PostModel (val posts: HashMap<String, Boolean> = HashMap())
class MyPageActivity : AppCompatActivity() {
    var postlists : ArrayList<String> = arrayListOf()
    private lateinit var binding: ActivityMypageBinding
    val posts: MutableList<Post> = mutableListOf()
    inner class application(val postid: String? = null)
    private lateinit var database : DatabaseReference
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.home.setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("/Users/Influencers/$uid")
            database.child("Applications").child("-Mp1I85uvTaM0RHpOHSh").get().addOnSuccessListener{
                    if(it.exists()){
                        val name = it.key
                        Toast.makeText(this, "$name", Toast.LENGTH_SHORT).show()
                    }else{
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
                }


            val intent = Intent(this, InfluencerMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.editbtn.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }
        binding.influencerLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MyPageActivity, MainActivity::class.java))
            Toast.makeText(this, "Successfully log-out", Toast.LENGTH_SHORT).show()
            finish()
        }


        readData(uid)


        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        applicationRecycler.layoutManager = layoutManager
        applicationRecycler.adapter = InfluencerAdapter()


// 여기서 자기가 올린거에 해당하는 거만 불러오게 만들기!! ( 지금 이거는 influencer에 똑같이 적용하면 될듯)
        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("/Applications/$uid/uid").equalTo(uid).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let { snapshot ->
                    val post = snapshot.getValue(Post::class.java)
                    post?.let {
                        if (prevChildKey == null) {
                            posts.add(it)
                            applicationRecycler.adapter?.notifyItemInserted(posts.size - 1)
                        } else {
                            val prevIndex = posts.map { it.postId }.indexOf(prevChildKey)
                            posts.add(prevIndex + 1, post)
                            applicationRecycler.adapter?.notifyItemInserted(prevIndex + 1)
                        }

                    }
                }
            }

                override fun onChildChanged(snapshot: DataSnapshot, prevChildKey: String?) {
                    snapshot?.let {
                        val post = snapshot.getValue(Post::class.java)
                        post?.let {
                            val prevIndex = posts.map { it.postId }.indexOf(prevChildKey)
                            posts[prevIndex + 1] = post
                            applicationRecycler.adapter?.notifyItemChanged(prevIndex + 1)
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
                    snapshot?.let {
                        val post = snapshot.getValue(Post::class.java)

                        post?.let {
                            val existIndex = posts.map { it.postId }.indexOf(post.postId)
                            posts.removeAt(existIndex)
                            applicationRecycler.adapter?.notifyItemRemoved(existIndex)

                            if (prevChildKey == null) {
                                posts.add(post)
                                applicationRecycler.adapter?.notifyItemChanged(posts.size - 1)
                            } else {
                                val prevIndex = posts.map { it.postId }.indexOf(prevChildKey)
                                posts.add(prevIndex + 1, post)
                                applicationRecycler.adapter?.notifyItemChanged(prevIndex + 1)
                            }
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    snapshot?.let {
                        val post = snapshot.getValue(Post::class.java)
                        post?.let { post ->
                            val existIndex = posts.map { it.postId }.indexOf(post.postId)
                            posts.removeAt(existIndex)
                            applicationRecycler.adapter?.notifyItemRemoved(existIndex)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error?.toException()?.printStackTrace()
                }
            })

    }

    private fun readData(uid : String){
        database = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val snsid = it.child("sns").value
                val name = it.child("username").value
                val imageuri = it.child("image").value
                binding.influencerName.text = name.toString()
                binding.influencersnsid.text = snsid.toString()
                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.imageView)

            }else{
            }
        }.addOnFailureListener {
            Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
        }

    }

    inner class InfluencerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff
        val who : TextView = itemView.writers

    }

    inner class InfluencerAdapter: RecyclerView.Adapter<InfluencerViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfluencerViewHolder {
            return InfluencerViewHolder(LayoutInflater.from(this@MyPageActivity).inflate(R.layout.posts,parent,false))
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


