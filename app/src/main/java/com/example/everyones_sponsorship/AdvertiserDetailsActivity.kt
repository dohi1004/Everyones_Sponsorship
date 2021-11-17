package com.example.everyones_sponsorship

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.databinding.ActivityAddPhotoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.posts.view.*

class AdvertiserDetailsActivity : AppCompatActivity() {

    val applicationlist = mutableListOf<Applications>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getStringExtra("postId")

        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recycler_application.layoutManager = layoutManager
//        recycler_application.adapter = AdvertiserAdapter()

        FirebaseDatabase.getInstance().getReference("/Posts/$postId")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot?.let {
                        val post = it.getValue(Post::class.java)
                        post?.let {
                            Picasso.get().load(it.image)
                        }
                    }
                }


            })

//        inner class DetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            val imageView: ImageView = itemView.findViewById(R.id.productimage)
//            val contentsText: TextView = itemView.productname
//            val timeTextView: TextView = itemView.timediff
//            val who: TextView = itemView.writers
//
//        }
//
//        inner class AdvertiserAdapter : RecyclerView.Adapter<DetailsViewHolder>() {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
//                return DetailsViewHolder(
//                    LayoutInflater.from(this@AdvertiserDetailsActivity)
//                        .inflate(R.layout.posts, parent, false)
//                )
//            }
//
//            override fun getItemCount(): Int {
//                return posts.size
//            }
//
//            override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
//                val post = posts[position]
//                Picasso.get().load(Uri.parse(post.image)).fit().centerCrop().into(holder.imageView)
//                holder.contentsText.text = post.productname
//                holder.who.text = post.postId
//
//                holder.itemView.setOnClickListener {
//                    val intent = Intent(
//                        this@AdvertiserDetailsActivity,
//                        AdvertiserDetailsActivity::class.java
//                    )
//                    intent.putExtra("postId", post.postId)
//                    startActivity(intent)
//                }
//            }
//    }



    }
}
