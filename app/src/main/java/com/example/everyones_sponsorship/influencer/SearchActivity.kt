package com.example.everyones_sponsorship.influencer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivitySearchresultBinding

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySearchresultBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}