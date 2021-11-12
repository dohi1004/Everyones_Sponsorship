package com.example.everyones_sponsorship

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.everyones_sponsorship.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener{
            if(binding.username.text.isNullOrBlank()&&binding.password.text.isNullOrBlank()){
                Toast.makeText(this, "Please fill the required fields", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "${binding.username.text} is logged in", Toast.LENGTH_SHORT).show()

            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, 1)
        }

        binding.registerInfluencer.setOnClickListener{
            val intent2 = Intent(this, SignInfluencer::class.java)
            startActivity(intent2)
        }

        binding.registerAdvertiser.setOnClickListener{
            val intent3 = Intent(this, SignAdvertiser::class.java)
            startActivity(intent3)
            //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }



    }
}