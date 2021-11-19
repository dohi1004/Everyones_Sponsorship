package com.example.everyones_sponsorship

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.everyones_sponsorship.databinding.ActivityAddPhotoBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    private lateinit var binding : ActivityAddPhotoBinding
    private lateinit var database : DatabaseReference
    val categoryList = listOf("Clothes","Sports","Game","Pet","Book","Furniture","Food","Device","Beauty")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.uploadbtn.setText("edit")
        var newcategory = ""
        val seekBar = binding.ratings
        var newratings = 0
        // 기존과 변화있는지 확인해서 없으면 이 값들 그대로 쓸 것
        val postId = intent.getStringExtra("postId")
        val originalproductname = intent.getStringExtra("productname")
        val originalcategory = intent.getStringExtra("category")
        val originalimage = intent.getStringExtra("image")
        val originalmessage = intent.getStringExtra("message")
        if (postId != null) {
            readData(postId)
        }
        // rating 새로 설정 받기
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when(seekBar?.progress){
                    0 -> {
                        newratings = 1
                    }
                    1 -> {
                        newratings = 2
                    }
                    2 -> {
                        newratings = 3
                    }
                    3 -> {
                        newratings = 4
                    }
                    4 -> {
                        newratings = 5
                    }

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        val myAdapter = ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,categoryList)
        //  category 새로 설정 받기
        binding.spinner.adapter = myAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when(position){
                    0 -> {
                        newcategory = "Clothes"
                    }
                    1 -> {
                        newcategory = "Sports"
                    }
                    2 -> {
                        newcategory = "Game"
                    }
                    3 -> {
                        newcategory = "Pet"
                    }
                    4 -> {
                        newcategory = "Book"
                    }
                    5 -> {
                        newcategory = "Furniture"
                    }
                    6 -> {
                        newcategory = "Food"
                    }
                    7 -> {
                        newcategory = "Device"
                    }
                    8 -> {
                        newcategory = "Beauty"
                    }

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        binding.realupload.setOnClickListener{
            //Initiate
            storage = FirebaseStorage.getInstance()
            auth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            //Open the album
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent,PICK_IMAGE_FROM_ALBUM)
        }
        //add image upload event
        binding.uploadbtn.setOnClickListener {
            var productdescription = binding.ProductDescription.text.toString()
            var productname = binding.ProductTitle.text.toString()
            if(newcategory == "")newcategory = originalcategory.toString()
            if(productname == "")productname = originalproductname.toString()
            if(productdescription == "")productdescription = originalmessage.toString()
            if(photoUri.toString()=="null")photoUri = originalimage.toString().toUri()
            contentUpload(productdescription,newratings, productname,newcategory, postId.toString())
            val intent = Intent(this,AdvertiserMainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.back.setOnClickListener {
            val intent = Intent(this,AdvertiserMainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                //This is path to the selected image
                photoUri = data?.data
                addphoto_image.setImageURI(photoUri)
            }else{
                //Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }
        }
    }

    // 게시글 수정 위한 update
    fun contentUpload(description: String, rating: Int, productname: String, category:String, postId: String){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener {
        }
        var post = mutableMapOf<String,Any>()
        post["productname"]=productname
        post["category"]=category
        post["image"]=photoUri.toString()
        post["message"]=description
        post["rating"]=rating
        post["postId"]=postId
        Log.d("itm","$rating")

        val database = FirebaseDatabase.getInstance().getReference("/Posts")
        database.child(postId).updateChildren(post).addOnSuccessListener {
            Toast.makeText(this, "Edit complete", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Edit fail", Toast.LENGTH_SHORT).show()
        }
        finish()

    }

    private fun readData(postId: String){
        database = FirebaseDatabase.getInstance().getReference("/Posts")
        database.child(postId.toString()).get().addOnSuccessListener {
            if(it.exists()){
                var position = 0
                val productname = it.child("productname").value
                val productdescription = it.child("message").value
                val imageuri = it.child("image").value
                val ratings : Int = it.child("rating").value.toString().toInt()
                val category = it.child("category").value.toString()
                binding.ProductTitle.setHint(productname.toString())
                binding.ProductDescription.setHint(productdescription.toString())
                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.addphotoImage)
                binding.ratings.progress = ratings-1
                for(i in categoryList){
                    if(category == i){
                        break
                    }
                    else{
                        position += 1
                    }
                }
                binding.spinner.setSelection(position)

            }else{
            }
        }.addOnFailureListener {
            Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
        }

    }
}