package com.example.everyones_sponsorship

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.databinding.ActivityAddPhotoBinding
import com.example.everyones_sponsorship.databinding.ActivityApplicationlistBinding
import com.example.everyones_sponsorship.databinding.ActivityProductinfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.activity_applicationlist.*
import kotlinx.android.synthetic.main.influencer_info.view.*
import kotlinx.android.synthetic.main.posts.view.*

class AdvertiserApplicationActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    val applications = mutableListOf<Application>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityApplicationlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val influencerId = 0
        val postId = intent.getStringExtra("postId")
        if (postId != null) {
            readData(postId, influencerId.toString())
        }
        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = MyAdapter(this,applications)

    }


    private fun readData(postId: String, influencerId : String){
        database = FirebaseDatabase.getInstance().getReference("/Posts/$postId/Applications/$influencerId")
        database.addValueEventListener(object :ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot?.let{
                    val application = it.getValue(Application::class.java)
                    Log.d("ITM","${application?.image}")
                    Log.d("ITM","$application")
                    if (application != null) {
                        applications.add(application)
                        recyclerview.adapter?.notifyDataSetChanged()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdvertiserApplicationActivity,
                    error.message, Toast.LENGTH_SHORT).show()
            }
        })
        }


}
