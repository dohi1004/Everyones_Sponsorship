package com.example.everyones_sponsorship.influencer


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.everyones_sponsorship.Post
import com.example.everyones_sponsorship.advertiser.AdvertiserMainActivity
import com.example.everyones_sponsorship.databinding.ActivityAddPhotoBinding
import com.example.everyones_sponsorship.databinding.ActivityInfluencerProfileBinding
import com.example.everyones_sponsorship.databinding.ActivityInfluencermainBinding
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


class ProfileEditActivity: AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityInfluencerProfileBinding
    var originalname = ""
    var originalmessage = ""
    var originalimage = ""
    var originalsns = ""
    var originalrating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfluencerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readData(uid)

        binding.backbtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
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
            var username = binding.name.text.toString()
            var snsid = binding.snsid.text.toString()
            var info = binding.info.text.toString()

            val temp : DatabaseReference = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
            temp.child(uid).get().addOnSuccessListener {
                if (username == "") username = originalname
                if (snsid == "") snsid = originalsns
                if (info == "") info = originalmessage
                if (photoUri.toString() == "null") photoUri = originalimage.toString().toUri()
                Update(info, username, uid, snsid, originalrating)
                val intent = Intent(this, MyPageActivity::class.java)
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
                val info = it.child("info").value
                val imageuri = it.child("image").value
                val snsid = it.child("sns").value
                val rating = it.child("rating").value
                originalname = name.toString()
                originalmessage = info.toString()
                originalsns = snsid.toString()
                originalimage = imageuri.toString()
                originalrating = rating.toString().toInt()

                binding.name.setHint(name.toString())
                binding.info.setHint(info.toString())
                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.profile)
                binding.snsid.setHint(snsid.toString())

            }else{
            }
        }.addOnFailureListener {
            Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
        }

    }
    fun Update(
        description: String,
        username: String,
        userId: String,
        snsId: String,
        rating: Int
    ) {
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)
            ?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener {
            }
        var influencer = mutableMapOf<String, Any>()
        influencer["uid"] = userId
        influencer["info"] = description
        influencer["image"] = photoUri.toString()
        influencer["rating"] = rating
        influencer["username"] = username
        influencer["sns"] = snsId
        // 인플루언서 업데이트
        val database = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
        database.child(uid).updateChildren(influencer).addOnSuccessListener {
            Toast.makeText(this, "Edit complete", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Edit fail", Toast.LENGTH_SHORT).show()
        }
        // post 업데이트
        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("Applications/$uid/uid").equalTo(uid).get().addOnSuccessListener {
        }

        finish()

    }
}
