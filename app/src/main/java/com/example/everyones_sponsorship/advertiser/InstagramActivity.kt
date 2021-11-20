package com.example.everyones_sponsorship.advertiser

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityInstagramBinding

class InstagramActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInstagramBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sns = intent.getStringExtra("sns")
        binding.instagram.apply {
            var webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }

        binding.instagram.loadUrl("https://www.instagram.com/$sns/")
    }

    override fun onBackPressed() {
        val binding = ActivityInstagramBinding.inflate(layoutInflater)
        if (binding.instagram.canGoBack())
        {
            binding.instagram.goBack()
        }
        else
        {
            finish()
        }
    }
}
