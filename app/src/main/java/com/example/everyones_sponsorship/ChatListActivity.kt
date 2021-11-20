package com.example.everyones_sponsorship

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityChatlistBinding
import com.google.firebase.database.FirebaseDatabase

class ChatListActivity : AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = Intent(this, ChattingActivity::class.java)
        intent.putExtra("destinationUid", "InfluencerName")
        startActivity(intent)
    }
}