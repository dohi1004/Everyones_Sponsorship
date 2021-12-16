package com.example.everyones_sponsorship.influencer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.Influencer
import com.example.everyones_sponsorship.Post
import com.example.everyones_sponsorship.Review
import com.example.everyones_sponsorship.databinding.ActivityReviewBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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

            val ref = FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews/")

            var numbers: ArrayList<Float> = arrayListOf() // Change to whatever type is accurate in your case
            var sum = 0.0
            var average = 0.0
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val rt = it.child("rating").value.toString().toFloat()
                        Log.d("ITM1", rt.toString())
                        numbers.add(rt)

                        average = numbers.average()
                        Log.d("ITM2", average.toString())
                    }
                    average = numbers.average()
                    Log.d("ITM3", numbers.toString())
                    p0.child("rating").getRef().setValue(average)
                    // After the forEach loop is finished you should have all the ratings in the numbers array
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            val review = Review(rating = rating.toString(), text = reviewtext.toString(), username = username, uid = uid)

            FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews").child(uid).setValue(review).addOnSuccessListener {
            }.addOnFailureListener {
                Toast.makeText(this, "Review fail", Toast.LENGTH_SHORT).show()
            }

            FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews").child(uid).setValue(review).addOnSuccessListener {
            }.addOnFailureListener {
                Toast.makeText(this, "Review fail", Toast.LENGTH_SHORT).show()
            }

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