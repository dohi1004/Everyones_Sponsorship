package com.example.everyones_sponsorship

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityCategorydetailsBinding

class CategoryDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCategorydetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = intent.getStringExtra("category")



    }
}