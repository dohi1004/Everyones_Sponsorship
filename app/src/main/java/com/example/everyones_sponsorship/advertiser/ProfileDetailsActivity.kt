package com.example.everyones_sponsorship.advertiser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.chat.ChattingActivity
import com.example.everyones_sponsorship.databinding.ActivityProfileDetailsBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class ProfileDetailsActivity : AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val rating = intent.getStringExtra("rating")
        val name = intent.getStringExtra("name")
        val snsid = intent.getStringExtra("snsid")
        val info = intent.getStringExtra("info")
        val image = intent.getStringExtra("image")
        val uid = intent.getStringExtra("uid")
        val postId = intent.getStringExtra("postId")

        binding.backbtn.setOnClickListener {
            val intent = Intent(this,AdvertiserMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.chatbtn.setOnClickListener {
            // var adv_uid = Firebase.auth.currentUser?.uid.toString() // advertiser의 user id
            var inf_uid = uid
            val intent = Intent(this@ProfileDetailsActivity, ChattingActivity::class.java)
            intent.putExtra("destinationUid", inf_uid)
            intent.putExtra("postId",postId)
            intent.putExtra("whoami","Advertisers")
            startActivity(intent)
        }

        binding.name.text = name
        binding.ratings.text = rating
        binding.instagram.setOnClickListener {
            val intent = Intent(this,InstagramActivity::class.java)
            intent.putExtra("sns",snsid)
            startActivity(intent)
        }
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage!!.reference
        var imageFileName = "IMAGE_" + uid + "_.png"
        storageRef!!.child("images").child(imageFileName)?.downloadUrl?.addOnSuccessListener { uri->
            Picasso.get().load(uri).fit().centerCrop().into(binding.image)
        }
    }
}