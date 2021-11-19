package com.example.everyones_sponsorship.advertiser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.everyones_sponsorship.Post
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.databinding.ActivityAddPhotoBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productdescription = binding.ProductDescription.text
        val productname = binding.ProductTitle.text

        val seekBar = binding.ratings
        // initial rating
        var ratings = 3
        var category = ""

        // seekbar for ratings
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                when(seekBar?.progress){
                    0 -> {
                        ratings = 1
                    }
                    1 -> {
                        ratings = 2
                    }
                    2 -> {
                        ratings = 3
                    }
                    3 -> {
                        ratings = 4
                    }
                    4 -> {
                        ratings = 5
                    }

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        //spinner for category
        val categoryList = listOf<String>("Clothes","Sports","Game","Pet","Book","Furniture","Food","Device","Beauty")
        val myAdapter = ArrayAdapter<String>(this,
            R.layout.support_simple_spinner_dropdown_item,categoryList)

        binding.spinner.adapter = myAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                when(position){
                    0 -> {
                        category = "Clothes"

                    }
                    1 -> {
                        category = "Sports"
                    }
                    2 -> {
                        category = "Game"
                    }
                    3 -> {
                        category = "Pet"
                    }
                    4 -> {
                        category = "Book"
                    }
                    5 -> {
                        category = "Furniture"
                    }
                    6 -> {
                        category = "Food"
                    }
                    7 -> {
                        category = "Device"
                    }
                    8 -> {
                        category = "Beauty"
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
            contentUpload(productdescription.toString(),ratings, productname.toString(),category)
            Toast.makeText(this, "test.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdvertiserMainActivity::class.java)
            startActivity(intent)


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
    fun contentUpload(description: String, rating : Int, productname : String, category:String){
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        val database = FirebaseDatabase.getInstance().getReference("Posts").push()
        val post = Post()
        post.postId = database.key.toString()
        post.productname = productname
        post.message = description
        post.image = photoUri.toString()
        post.writeTime = ServerValue.TIMESTAMP
        post.rating = rating
        post.category = category

        database.setValue(post)
        Toast.makeText(this, "Upload complete", Toast.LENGTH_SHORT).show()
        finish()

        //Promise method
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener {
            Toast.makeText(this, "storage", Toast.LENGTH_SHORT).show()
        }
    }



}
