package com.example.everyones_sponsorship.influencer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.*
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.databinding.ActivityProductinfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProductinfoBinding
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductinfoBinding.inflate(layoutInflater)

        val userId =  FirebaseAuth.getInstance().currentUser!!.uid

        setContentView(binding.root)
        // intent로 postId 넘겨 받기 -> post 유저 별 저장하려고 하는데..
        val postId = intent.getStringExtra("postId")
        val productname = intent.getStringExtra("productname")
        val category = intent.getStringExtra("category")
        val image = intent.getStringExtra("image")
        val message = intent.getStringExtra("message")
        val time = intent.getStringExtra("timestamp")
        val ratings = intent.getStringExtra("rating")

        binding.productname.text = productname.toString()
        binding.Productdetails.text = message.toString()
        Picasso.get().load(Uri.parse(image.toString())).fit().centerCrop().into(binding.storedproductimage)

        // 뒤로가기
        binding.backbtn.setOnClickListener {
            val intent = Intent(this@ProductDetailsActivity, InfluencerMainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // 신청하기
        binding.applybtn.setOnClickListener {
            // 팝업 창 -> 유저에게 재확인 요청
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_apply_check, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            //  no 누르면 취소했다는 내용 + 다시 product detail 보임
            val noButton = mDialogView.findViewById<Button>(R.id.closeButton)
            noButton.setOnClickListener {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
                mAlertDialog.dismiss()
            }
            //  yes 누르면 신청 완료됨 -> 이 목록도 파이어베이스에 저장됨 -> post id 에따라 신청한 influencer id
            val yesButton = mDialogView.findViewById<Button>(R.id.successButton)
            yesButton.setOnClickListener {
                database = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
                database.child(userId).get().addOnSuccessListener {
                    if(it.exists()){
                        val name = it.child("username").value
                        val info = it.child("info").value
                        val imageuri = it.child("image").value
                        val sns = it.child("sns").value
                        val rating = it.child("rating").value
                        val uid = it.child("uid").value
                        val user = Influencer(username=name.toString(),INFO=info.toString(),rating = rating.toString().toInt(), sns = sns.toString(), image = imageuri.toString(), uid = uid.toString())
                        // 해당 post id 에 influencer id를 unique 값으로 해서 influencer 정보 넘긴다!
                        FirebaseDatabase.getInstance().getReference("/Posts/$postId/Applications").child(userId).setValue(user).addOnSuccessListener {
                        }.addOnFailureListener {
                            Toast.makeText(this, "Application fail", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this@ProductDetailsActivity, InfluencerMainActivity::class.java)
                Toast.makeText(this, "Application success", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
            }
        }


    }

}