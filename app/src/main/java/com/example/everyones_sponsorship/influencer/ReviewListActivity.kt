package com.example.everyones_sponsorship.influencer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.Post
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.Review
import com.example.everyones_sponsorship.Reviews
import com.example.everyones_sponsorship.databinding.ActivityReviewBinding
import com.example.everyones_sponsorship.databinding.ActivityReviewlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.activity_reviewlist.*
import kotlinx.android.synthetic.main.posts.view.*
import kotlinx.android.synthetic.main.reviews.view.*
import org.w3c.dom.Text

class ReviewListActivity : AppCompatActivity() {
    val reviews: MutableList<Reviews> = mutableListOf()
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReviewlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager = LinearLayoutManager(this)
        val postId = intent.getStringExtra("postId")

        binding.influencertoolbar.title = "Review List"

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = ReviewAdapter()

        FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let { snapshot ->
                    val review = snapshot.getValue(Reviews::class.java)
                    review?.let {
                        if (prevChildKey == null) {
                            reviews.add(it)
                            recyclerview.adapter?.notifyItemInserted(reviews.size - 1)
                        } else {
                            val prevIndex = reviews.map { it.uid }.indexOf(prevChildKey)
                            reviews.add(prevIndex + 1, review)
                            recyclerview.adapter?.notifyItemInserted(prevIndex + 1)
                        }

                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let {
                    val review = snapshot.getValue(Reviews::class.java)
                    review?.let {
                        val prevIndex = reviews.map { it.uid }.indexOf(prevChildKey)
                        reviews[prevIndex + 1] = review
                        recyclerview.adapter?.notifyItemChanged(prevIndex + 1)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let {
                    val review = snapshot.getValue(Reviews::class.java)

                    review?.let {
                        val existIndex = reviews.map { it.uid }.indexOf(review.uid)
                        reviews.removeAt(existIndex)
                        recyclerview.adapter?.notifyItemRemoved(existIndex)

                        if (prevChildKey == null) {
                            reviews.add(review)
                            recyclerview.adapter?.notifyItemChanged(reviews.size - 1)
                        } else {
                            val prevIndex = reviews.map { it.uid }.indexOf(prevChildKey)
                            reviews.add(prevIndex + 1, review)
                            recyclerview.adapter?.notifyItemChanged(prevIndex + 1)
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot?.let {
                    val review = snapshot.getValue(Reviews::class.java)
                    review?.let { post ->
                        val existIndex = reviews.map { it.uid }.indexOf(review.uid)
                        reviews.removeAt(existIndex)
                        recyclerview.adapter?.notifyItemRemoved(existIndex)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error?.toException()?.printStackTrace()
            }
        })



    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val username : TextView = itemView.influencername
        val rating : TextView = itemView.ratings
    }

    inner class ReviewAdapter: RecyclerView.Adapter<ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(this@ReviewListActivity).inflate(R.layout.reviews,parent,false))
        }

        override fun getItemCount(): Int {
            return reviews.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val review = reviews[position]
            holder.username.text = review.username
            holder.rating.text = review.rating

            holder.itemView.setOnClickListener {
                val mDialogView = LayoutInflater.from(this@ReviewListActivity).inflate(R.layout.dialog_reviewdetails, null)
                val mBuilder = AlertDialog.Builder(this@ReviewListActivity)
                    .setView(mDialogView)
                val reviewmessage = mDialogView.findViewById<TextView>(R.id.reviewmessage)
                reviewmessage.setText(review.text)
                val reviewer = mDialogView.findViewById<TextView>(R.id.name)
                reviewer.setText(review.username)
                mBuilder.show()
            }

        }

    }
}