package com.example.everyones_sponsorship.influencer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.Influencer
import com.example.everyones_sponsorship.Post
import com.example.everyones_sponsorship.Review
import com.example.everyones_sponsorship.databinding.ActivityReviewBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_review.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val postId = intent.getStringExtra("postId")
        val username = intent.getStringExtra("username")
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        binding.ratingBar.rating = 0.0F

        binding.backback.setOnClickListener {
            val intent = Intent(this,MyPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.reviewbtn.setOnClickListener {
            val reviewtext = binding.editText.text
            val rating = binding.ratingBar.rating
            val review = Review(rating = rating.toString(), text = reviewtext.toString(), username = username, uid = uid)

            FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews").child(uid).setValue(review).addOnSuccessListener {
            }.addOnFailureListener {
                Toast.makeText(this, "Review fail", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this,MyPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}