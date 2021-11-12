package com.example.everyones_sponsorship

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.everyones_sponsorship.databinding.ActivitySignInfluencerBinding

class SignInfluencerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignInfluencerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // db에 id, pwd 저장하는 로직 구현

        binding.submit.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}