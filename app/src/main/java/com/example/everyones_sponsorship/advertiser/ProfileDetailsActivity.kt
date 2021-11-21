package com.example.everyones_sponsorship.advertiser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.everyones_sponsorship.ChatModel
import com.example.everyones_sponsorship.ChattingActivity
import com.example.everyones_sponsorship.databinding.ActivityProfileDetailsBinding
import com.example.everyones_sponsorship.fragment.ChatFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class ProfileDetailsActivity : AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val rating = intent.getStringExtra("rating")
        val snsid = intent.getStringExtra("snsid")
        val info = intent.getStringExtra("info")
        val image = intent.getStringExtra("image")
        val uid = intent.getStringArrayExtra("uid")

        binding.backbtn.setOnClickListener {
            val intent = Intent(this,AdvertiserApplicationActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.chatbtn.setOnClickListener {
            // var adv_uid = Firebase.auth.currentUser?.uid.toString() // advertiser의 user id
            var inf_uid = uid
            var ms = "ntFl6D41tedQopUqE9OP11cFTuu1"
            val intent = Intent(this@ProfileDetailsActivity, ChattingActivity::class.java)
            intent.putExtra("destinationUid", ms)
            startActivity(intent)
        }

        binding.name.text = name
        binding.ratings.text = rating
        binding.instagram.setOnClickListener {
            val intent = Intent(this,InstagramActivity::class.java)
            intent.putExtra("sns",snsid)
            startActivity(intent)
        }
//        Picasso.get().load(Uri.parse(image.toString())).fit().centerCrop().into(binding.image)
    }
}