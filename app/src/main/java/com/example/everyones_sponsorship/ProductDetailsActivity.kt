package com.example.everyones_sponsorship

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityProductinfoBinding
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.search_bar.view.*

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProductinfoBinding
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductinfoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // intent로 postId 넘겨 받기 -> 이 postId와 관련된 게시물 내용 업로드 위함!
        val postId = intent.getStringExtra("postId")

        if (postId != null) {
            readData(postId)
        }

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
                // 해당 post id 에 influencer id를 unique 값으로 해서 influencer 정보 넘긴다!
                database = FirebaseDatabase.getInstance().getReference("/Posts/$postId/Applications")
                val userId = 0
                val user = Application()
                user.influencerId = userId
                database.child(userId.toString()).setValue(user).addOnSuccessListener {
                }.addOnFailureListener {
                    Toast.makeText(this, "Application fail", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(this@ProductDetailsActivity, InfluencerMainActivity::class.java)
                Toast.makeText(this, "Application success", Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        }


    }
    // 파이어베이스에서 해당 post id에 해당하는 사진과 product info 불러와서 화면에 보여줌
    private fun readData(postId: String){
        database = FirebaseDatabase.getInstance().getReference("/Posts")
        database.child(postId).get().addOnSuccessListener {
            if(it.exists()){
                val productname = it.child("productname").value
                val productdescription = it.child("message").value
                val imageuri = it.child("image").value
                binding.productname.text = productname.toString()
                binding.Productdetails.text = productdescription.toString()
                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.storedproductimage)

            }else{
            }
        }.addOnFailureListener {
            Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
        }

    }



}