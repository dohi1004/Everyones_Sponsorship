package com.example.everyones_sponsorship.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.everyones_sponsorship.Influencer
import com.example.everyones_sponsorship.databinding.ActivitySignInfluencerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInfluencerActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignInfluencerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // db에 id, pwd 저장하는 로직 구현


        binding.submit.setOnClickListener{
            when{
                TextUtils.isEmpty(binding.usernameInfluencer.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@SignInfluencerActivity,
                        "Please enter username.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.passwordInfluencer.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@SignInfluencerActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(binding.SNSId.text.toString().trim{it<=' '}) -> {
                    Toast.makeText(
                        this@SignInfluencerActivity,
                        "Please enter InstagramID.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = binding.usernameInfluencer.text.toString().trim{it <= ' '}
                    val password: String = binding.passwordInfluencer.text.toString().trim{it <= ' '}
                    val snsid: String = binding.SNSId.text.toString().trim{it <= ' '}

//                    database = FirebaseDatabase.getInstance().getReference("Users")
//                    val Influencer = Influencer(email, password, snsid, true)
//                    database.child(snsid).setValue(Influencer).addOnSuccessListener {
//                        Toast.makeText(
//                            this@SignInfluencerActivity,
//                            "You are registered successfully.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        val intent = Intent(this@SignInfluencerActivity, MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        intent.putExtra("user_id", database.key)
//                        intent.putExtra("email_id", email)
//                        startActivity(intent)
//                        finish()
//                    }.addOnFailureListener {
//                        Toast.makeText(
//                            this@SignInfluencerActivity,
//                            "You can not register.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }


                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this@SignInfluencerActivity,
                                    "You are registered successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@SignInfluencerActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)

                                val influencer = Influencer(username = email, sns = snsid, INFO = "", image ="content://media/external/images/media/31", uid = firebaseUser.uid)
                                database = FirebaseDatabase.getInstance().getReference("Users/Influencers")
                                database.child(firebaseUser.uid).setValue(influencer).addOnSuccessListener {
                                }.addOnFailureListener {  }
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@SignInfluencerActivity,
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