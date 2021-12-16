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
import com.example.everyones_sponsorship.chat.ChatModel
import com.example.everyones_sponsorship.databinding.ActivityReviewBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_review.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewActivity: AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference
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
            var totalrating = 0.0
            var n = 0

            FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews").child(uid).setValue(review).addOnSuccessListener {
            }.addOnFailureListener {
                Toast.makeText(this, "Review fail", Toast.LENGTH_SHORT).show()
            }

            val ref = FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews")
            var numbers: ArrayList<Float> = arrayListOf() // Change to whatever type is accurate in your case
            var average = 0.0
            Log.d("ITM","HI")
            ref.get().addOnSuccessListener {
                it.children.forEach(){
                    Log.d("Hello","value ${it.value}")
                    Log.d("Hello","child ${it.child("rating").value}")
                    totalrating += it.child("rating").value.toString().toFloat()
                    n += 1
                    Log.d("Hello","totalrating $totalrating")
                }
                Log.d("Hello","n $n")
                if(n!=0){
                    average = totalrating/n
                    Log.d("Hello","average $average")
                }

                FirebaseDatabase.getInstance().getReference("/Posts/$postId/rating").setValue(average).addOnSuccessListener {
                }.addOnFailureListener{}

            }.addOnFailureListener {  }





//            ref.child("Reviews").get().addOnSuccessListener {
//                Log.d("ITM", "${it.children}")
//                Log.d("ITM", "yeah")
//                    it.children.forEach {
//                        val rt = it.child("rating").value.toString().toFloat()
//                        numbers.add(rt)
//                    }
//
//            }.addOnFailureListener {
//                Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
//            }
//            Log.d("ITM", "$numbers")
//
//
//

            val intent = Intent(this,MyPageActivity::class.java)
            startActivity(intent)
            finish()
        }


        }
    }
