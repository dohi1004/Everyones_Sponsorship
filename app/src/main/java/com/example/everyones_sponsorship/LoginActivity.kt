package com.example.everyones_sponsorship

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.everyones_sponsorship.databinding.ActivityLoginBinding
import com.example.everyones_sponsorship.databinding.ActivityLoginEmptyBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginEmptyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setResult(RESULT_OK, intent)
        finish()
    }
}