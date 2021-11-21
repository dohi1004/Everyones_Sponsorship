package com.example.everyones_sponsorship.advertiser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.chat.ChattingActivity
import com.example.everyones_sponsorship.databinding.ActivityProfileDetailsBinding
import com.google.firebase.database.FirebaseDatabase
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

        binding.backbtn.setOnClickListener {
            val intent = Intent(this,AdvertiserApplicationActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "rating: $rating,name: $name, $snsid, $info, uid: $uid", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.chatbtn.setOnClickListener {
            // var adv_uid = Firebase.auth.currentUser?.uid.toString() // advertiser의 user id
            var inf_uid = uid
            val intent = Intent(this@ProfileDetailsActivity, ChattingActivity::class.java)
            intent.putExtra("destinationUid", inf_uid)
            startActivity(intent)
        }

        binding.name.text = name
        binding.ratings.text = rating
        binding.instagram.setOnClickListener {
            val intent = Intent(this,InstagramActivity::class.java)
            intent.putExtra("sns",snsid)
            startActivity(intent)
        }
        Picasso.get().load(Uri.parse(image.toString())).fit().centerCrop().into(binding.image)
    }
}