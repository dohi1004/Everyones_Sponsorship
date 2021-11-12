package com.example.everyones_sponsorship

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.everyones_sponsorship.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
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

                                val intent = Intent(this@MainActivity, InfluencerMainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
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