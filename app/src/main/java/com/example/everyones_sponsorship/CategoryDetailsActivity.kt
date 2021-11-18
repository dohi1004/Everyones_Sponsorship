package com.example.everyones_sponsorship

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityCategorydetailsBinding

class CategoryDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCategorydetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // recycler view 구현 -> 특정 category에만 있는 list를 반환하도록 짜기 ( firebase 부분)
        // search result -> 총 몇개의 게시물 있는 지 나타내기

        val category = intent.getStringExtra("category")
        val color = "#7C777B"
        binding.categoryname.text = category
        if(category == "Pet"){
            binding.categoryimage.setImageResource(R.drawable.ic_pet_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Clothes"){
            binding.categoryimage.setImageResource(R.drawable.ic_clothes_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Sports"){
            binding.categoryimage.setImageResource(R.drawable.ic_sports_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Furniture"){
            binding.categoryimage.setImageResource(R.drawable.ic_chair_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Food"){
            binding.categoryimage.setImageResource(R.drawable.ic_food_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Book"){
            binding.categoryimage.setImageResource(R.drawable.ic_book_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Game"){
            binding.categoryimage.setImageResource(R.drawable.ic_gamepad_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Device"){
            binding.categoryimage.setImageResource(R.drawable.ic_device_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Beauty"){
            binding.categoryimage.setImageResource(R.drawable.ic_air_freshener_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }



    }
}