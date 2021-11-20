package com.example.everyones_sponsorship

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityChatlistBinding

class ChatListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, ChattingActivity::class.java)
        intent.putExtra("destinationUid", "InfluencerName")
        startActivity(intent)
    }
}