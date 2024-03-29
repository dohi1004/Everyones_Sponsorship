package com.example.everyones_sponsorship.influencer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivitySearchBinding


class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setOnClickListener {
            val intent = Intent(this@SearchActivity,SearchresultActivity::class.java)
            var searchtext = binding.searchtext.text.toString()
            intent.putExtra("text",searchtext)
            startActivity(intent)
            finish()
        }



    }
}