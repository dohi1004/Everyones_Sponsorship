package com.example.everyones_sponsorship.start

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.everyones_sponsorship.advertiser.AdvertiserMainActivity
import com.example.everyones_sponsorship.databinding.ActivityMainBinding
import com.example.everyones_sponsorship.influencer.InfluencerMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var database2 : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener{

            when{
                TextUtils.isEmpty(binding.username.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter username.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.password.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val email: String = binding.username.text.toString().trim{it <= ' '}
                    val password: String = binding.password.text.toString().trim{it <= ' '}
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "You are logged in successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                                // 만약 유저가 influencer 인 경우 -> influencer main page를 킨다!
                                Log.d("itm","$uid")
                                database = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
                                database.child(uid).get().addOnSuccessListener {
                                    if(it.exists()){
                                        val intent = Intent(this@MainActivity, InfluencerMainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                        intent.putExtra("email_id", email)
                                        startActivity(intent)
                                        finish()
                                    }}.addOnFailureListener {}
                                // 만약 유저가 advertiser 인 경우 -> advertiser main page를 킨다!
                                database2 = FirebaseDatabase.getInstance().getReference("/Users/Advertisers")
                                database2.child(uid).get().addOnSuccessListener {
                                    if(it.exists()){
                                        val intent = Intent(this@MainActivity, AdvertiserMainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                        intent.putExtra("email_id", email)
                                        startActivity(intent)
                                        finish()
                                    }}.addOnFailureListener {}
                            } else { // if internet connection is lost or ex.
                                Toast.makeText(
                                    this@MainActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
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