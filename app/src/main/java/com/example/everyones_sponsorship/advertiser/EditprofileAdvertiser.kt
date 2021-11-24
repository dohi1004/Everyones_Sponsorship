package com.example.everyones_sponsorship.advertiser

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import com.example.everyones_sponsorship.databinding.ActivityEditprofileAdvertiserBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class EditprofileAdvertiser : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityEditprofileAdvertiserBinding
    var originalname = ""
    var originalmessage = ""
    var originalimage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditprofileAdvertiserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        readData(uid)

        binding.backbtn.setOnClickListener {
            val intent = Intent(this, AdvertiserMainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // 프로필 이미지 변경
        binding.upload.setOnClickListener {
            storage = FirebaseStorage.getInstance()
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            //Open the album
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        binding.editbtn.setOnClickListener {
            var username = originalname
            var business_id = binding.businessid.text.toString()

            val temp : DatabaseReference = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
            temp.child(uid).get().addOnSuccessListener {
                if (business_id == "") business_id = originalmessage
                if (photoUri.toString() == "null") photoUri = originalimage.toString().toUri()
                Update(business_id, uid, username)
                val intent = Intent(this, AdvertiserMainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
    private fun readData(userId: String){
        database = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
        database.child(userId).get().addOnSuccessListener {
            if(it.exists()){
                val name = it.child("username").value
                val business_id = it.child("business").value
                val imageuri = it.child("image").value

                originalname = name.toString()
                originalmessage = business_id.toString()
                originalimage = imageuri.toString()

                binding.businessid.setHint(business_id.toString())
                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.profile)

            }else{
            }
        }.addOnFailureListener {
            Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
        }

    }
    fun Update(
        business: String,
        uid: String,
        username: String
    ) {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)
            ?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener {
            }
        var Adver = mutableMapOf<String, Any>()
        Adver["business"] = business
        Adver["image"] = photoUri.toString()
        Adver["uid"] = uid
        Adver["username"] = username
        // 광고주 업데이트
        val database = FirebaseDatabase.getInstance().getReference("/Users/Advertisers")
        database.child(uid).updateChildren(Adver).addOnSuccessListener {
            Toast.makeText(this, "Edit complete", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Edit fail", Toast.LENGTH_SHORT).show()
        }
        finish()

    }
}