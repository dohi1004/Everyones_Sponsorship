//package com.example.everyones_sponsorship.advertiser
//
//import android.content.Intent
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Toast
//import androidx.core.net.toUri
//import com.example.everyones_sponsorship.Advertiser
//import com.example.everyones_sponsorship.databinding.ActivityEditprofileAdvertiserBinding
//import com.example.everyones_sponsorship.influencer.MyPageActivity
//import com.google.android.gms.tasks.Task
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.UploadTask
//import com.squareup.picasso.Picasso
//import java.text.SimpleDateFormat
//import java.util.*
//
//class EditprofileAdvertiser : AppCompatActivity() {
//    var PICK_IMAGE_FROM_ALBUM = 0
//    var storage: FirebaseStorage? = null
//    var photoUri: Uri? = null
//    var auth: FirebaseAuth? = null
//    var firestore: FirebaseFirestore? = null
//    val uid = FirebaseAuth.getInstance().currentUser!!.uid
//    private lateinit var database: DatabaseReference
//    private lateinit var binding: ActivityEditprofileAdvertiserBinding
//    var originalname = ""
//    var originalmessage = ""
//    var originalimage = ""
//    var originalsns = ""
//    var originalrating = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityEditprofileAdvertiserBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        readData(uid)
//
//        binding.backbtn.setOnClickListener {
//            val intent = Intent(this, MyPageActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//        // 프로필 이미지 변경
//        binding.upload.setOnClickListener {
//            storage = FirebaseStorage.getInstance()
//            auth = FirebaseAuth.getInstance()
//            firestore = FirebaseFirestore.getInstance()
//            //Open the album
//            var photoPickerIntent = Intent(Intent.ACTION_PICK)
//            photoPickerIntent.type = "image/*"
//            startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)
//        }
//
//        binding.editbtn.setOnClickListener {
//            var username = binding.name.text.toString()
//            var snsid = binding.sns.text.toString()
//            var info = binding.businessid.text.toString()
//
//            val temp : DatabaseReference = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
//            temp.child(uid).get().addOnSuccessListener {
//                if (username == "") username = originalname
//                if (snsid == "") snsid = originalsns
//                if (info == "") info = originalmessage
//                if (photoUri.toString() == "null") photoUri = originalimage.toString().toUri()
//                Update(info, username, uid, snsid)
//                val intent = Intent(this, AdvertiserMainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//
//        }
//
//    }
//    private fun readData(userId: String){
//        database = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
//        database.child(userId).get().addOnSuccessListener {
//            if(it.exists()){
//                val name = it.child("username").value
//                val info = it.child("info").value
//                val imageuri = it.child("image").value
//                val snsid = it.child("sns").value
//                val rating = it.child("rating").value
//                originalname = name.toString()
//                originalmessage = info.toString()
//                originalsns = snsid.toString()
//                originalimage = imageuri.toString()
//                originalrating = rating.toString().toInt()
//
//                binding.name.setHint(name.toString())
//                binding.businessid.setHint(info.toString())
//                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.profile)
//                binding.sns.setHint(snsid.toString())
//
//            }else{
//            }
//        }.addOnFailureListener {
//            Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
//        }
//
//    }
//    fun Update(
//        business: String,
//        password: String,
//        uid: String,
//        username: String
//    ) {
//        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        var imageFileName = "IMAGE_" + timestamp + "_.png"
//        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
//        storageRef?.putFile(photoUri!!)
//            ?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
//                return@continueWithTask storageRef.downloadUrl
//            }?.addOnSuccessListener {
//            }
//        var Adver = mutableMapOf<String, Any>()
//
//        Adver["info"] = description
//        Adver["image"] = photoUri.toString()
//        Adver["uid"] = userId
//        Adver["username"] = username
//        // 인플루언서 업데이트
//        val database = FirebaseDatabase.getInstance().getReference("/Users/Advertisers")
//        database.child(uid).updateChildren(Adver).addOnSuccessListener {
//            Toast.makeText(this, "Edit complete", Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener {
//            Toast.makeText(this, "Edit fail", Toast.LENGTH_SHORT).show()
//        }
//        // post 업데이트
//        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("Applications/$uid").equalTo(uid).get().addOnSuccessListener {
//            it.ref.updateChildren(Adver)
//        }
//
//
//
//        finish()
//
//    }
//}