package com.example.everyones_sponsorship

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityChattingBinding


class ChattingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChattingBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}