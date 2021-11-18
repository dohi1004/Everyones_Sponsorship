package com.example.everyones_sponsorship

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityCategoryBinding
import com.example.everyones_sponsorship.databinding.ActivityMainBinding

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clothes.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","clothes")
            startActivity(intent)
        }
        binding.pet.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","pet")
            startActivity(intent)
        }
        binding.games.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","game")
            startActivity(intent)
        }
        binding.sports.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","sports")
            startActivity(intent)
        }
        binding.food.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","food")
            startActivity(intent)
        }
        binding.furniture.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","furniture")
            startActivity(intent)
        }
        binding.device.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","device")
            startActivity(intent)
        }
        binding.beauty.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","beauty")
            startActivity(intent)
        }
        binding.book.setOnClickListener {
            val intent = Intent(this, CategoryDetailsActivity::class.java)
            intent.putExtra("category","book")
            startActivity(intent)
        }

    }
}