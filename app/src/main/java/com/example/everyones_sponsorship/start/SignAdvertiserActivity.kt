package com.example.everyones_sponsorship.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.everyones_sponsorship.Advertiser
import com.example.everyones_sponsorship.Influencer
import com.example.everyones_sponsorship.databinding.ActivitySignAdvertiserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_advertiser.*

class SignAdvertiserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var database : DatabaseReference
        val binding = ActivitySignAdvertiserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // db에 id, pwd 저장하는 로직 구현

        binding.submit.setOnClickListener{
            when{
                TextUtils.isEmpty(binding.usernameAdvertiser.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@SignAdvertiserActivity,
                        "Please enter username.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.passwordAdvertiser.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@SignAdvertiserActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.businessId.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@SignAdvertiserActivity,
                        "Please enter InstagramID.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = binding.usernameAdvertiser.text.toString().trim{it <= ' '}
                    val password: String = binding.passwordAdvertiser.text.toString().trim{it <= ' '}
                    val businessid: String = binding.businessId.text.toString().trim{it <= ' '}
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this@SignAdvertiserActivity,
                                    "You are registered successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@SignAdvertiserActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)

                                val advertiser = Advertiser(username = email, password = password, business = businessid)
                                database = FirebaseDatabase.getInstance().getReference("Users/Advertisers")
                                database.child(firebaseUser.uid).setValue(advertiser).addOnSuccessListener {
                                }.addOnFailureListener {  }

                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@SignAdvertiserActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }

        }
    }
}