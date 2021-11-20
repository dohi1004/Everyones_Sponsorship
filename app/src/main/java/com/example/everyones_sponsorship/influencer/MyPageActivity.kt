package com.example.everyones_sponsorship.influencer

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
import com.example.everyones_sponsorship.ListAdapter
import com.example.everyones_sponsorship.MyAdapter
import com.example.everyones_sponsorship.Post
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.advertiser.AdvertiserApplicationActivity
import com.example.everyones_sponsorship.advertiser.EditActivity
import com.example.everyones_sponsorship.databinding.ActivityAdvertiserMainBinding
import com.example.everyones_sponsorship.databinding.ActivityMypageBinding
import com.example.everyones_sponsorship.databinding.ActivityProductinfoBinding
import com.example.everyones_sponsorship.start.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.activity_applicationlist.*
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*

class MyPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBinding
    val posts: MutableList<Post> = mutableListOf()
    private lateinit var database : DatabaseReference
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.home.setOnClickListener {
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
        applicationRecycler.adapter = ListAdapter(this, posts)


// 여기서 자기가 올린거에 해당하는 거만 불러오게 만들기!! ( 지금 이거는 influencer에 똑같이 적용하면 될듯)
        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("writeTime")
            .addChildEventListener(object :
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


}
