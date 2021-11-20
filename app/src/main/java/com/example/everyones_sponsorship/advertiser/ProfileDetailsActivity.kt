package com.example.everyones_sponsorship.advertiser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityProfileDetailsBinding
import com.squareup.picasso.Picasso


class ProfileDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener {
            val intent = Intent(this,AdvertiserApplicationActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.chatbtn.setOnClickListener {
            // 채팅방 만들기 기능
        }

        val name = intent.getStringExtra("name")
        val rating = intent.getStringExtra("rating")
        val snsid = intent.getStringExtra("snsid")
        val info = intent.getStringExtra("info")
        val image = intent.getStringExtra("image")

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