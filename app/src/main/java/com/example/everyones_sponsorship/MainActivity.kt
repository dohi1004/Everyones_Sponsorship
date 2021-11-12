package com.example.everyones_sponsorship

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.everyones_sponsorship.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener{
            if(binding.username.text.isNullOrBlank()&&binding.password.text.isNullOrBlank()){
                Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "${binding.username.text} is logged in", Toast.LENGTH_SHORT).show()

            }
        }

        binding.registerinfluencer.setOnClickListener{
            val intent2 = Intent(this, SignInfluencerActivity::class.java)
            startActivity(intent2)
            val userId = intent2.getStringExtra("user_id")
            val emailId = intent2.getStringExtra("email_id")
            Log.d("ITM","${userId},${emailId}")


        }

        binding.registeradvertiser.setOnClickListener{
            val intent3 = Intent(this, SignAdvertiserActivity::class.java)
            startActivity(intent3)
        }



    }
}