package com.example.everyones_sponsorship.influencer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.everyones_sponsorship.*
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.advertiser.AdvertiserApplicationActivity
import com.example.everyones_sponsorship.advertiser.EditActivity
import com.example.everyones_sponsorship.databinding.ActivityAdvertiserMainBinding
import com.example.everyones_sponsorship.databinding.ActivityMypageBinding
import com.example.everyones_sponsorship.databinding.ActivityProductinfoBinding
import com.example.everyones_sponsorship.start.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.activity_applicationlist.*
import kotlinx.android.synthetic.main.activity_categorydetails.*
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*
class MyPageActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    var postlists : MutableList<Post> = mutableListOf()
    private lateinit var binding: ActivityMypageBinding
    val posts: MutableList<Post> = mutableListOf()
    var username = ""
    inner class application(val postid: String? = null)
    private lateinit var database : DatabaseReference
    private var review = false
    val uid = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.home.setOnClickListener {
            val intent = Intent(this, InfluencerMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.withdrawl.setOnClickListener {
            val mDialogView = LayoutInflater.from(this@MyPageActivity).inflate(R.layout.dialog_withdrawl_check, null)
            val mBuilder = AlertDialog.Builder(this@MyPageActivity)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            val nobtn = mDialogView.findViewById<Button>(R.id.no)
            nobtn.setOnClickListener {
                mAlertDialog.dismiss()
            }
            val yesbtn = mDialogView.findViewById<Button>(R.id.yes)

            yesbtn.setOnClickListener {

                database = FirebaseDatabase.getInstance().getReference("Users")
                database.child("Influencers/$uid").removeValue().addOnSuccessListener{}.addOnFailureListener{}

                FirebaseDatabase.getInstance().getReference("Posts").orderByChild("Applications/$uid").get().addOnSuccessListener {
                    for (i in postlists) {
                        val post = i
                        val postId = post.postId
                        FirebaseDatabase.getInstance().getReference("Posts").child("$postId/Applications/$uid").removeValue()
                    }
                }

                revokeAccess()
                mAlertDialog.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()

            }
        }


        binding.search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.editbtn.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.putExtra("username",username)
            startActivity(intent)
        }
        binding.influencerLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MyPageActivity, MainActivity::class.java))
            Toast.makeText(this, "Successfully log-out", Toast.LENGTH_SHORT).show()
            finish()
        }


        readData(uid)


        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        applicationRecycler.layoutManager = layoutManager
        applicationRecycler.adapter = InfluencerAdapter()


// 여기서 자기가 올린거에 해당하는 거만 불러오게 만들기!! ( 지금 이거는 influencer에 똑같이 적용하면 될듯)
        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("/Applications/$uid/uid").equalTo(uid).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let { snapshot ->
                    val post = snapshot.getValue(Post::class.java)
                    post?.let {
                        if (prevChildKey == null) {
                            posts.add(it)
                            applicationRecycler.adapter?.notifyItemInserted(posts.size - 1)
                        } else {
                            val prevIndex = posts.map { it.postId }.indexOf(prevChildKey)
                            posts.add(prevIndex + 1, post)
                            applicationRecycler.adapter?.notifyItemInserted(prevIndex + 1)
                        }

                    }
                }
            }

                override fun onChildChanged(snapshot: DataSnapshot, prevChildKey: String?) {
                    snapshot?.let {
                        val post = snapshot.getValue(Post::class.java)
                        post?.let {
                            val prevIndex = posts.map { it.postId }.indexOf(prevChildKey)
                            posts[prevIndex + 1] = post
                            applicationRecycler.adapter?.notifyItemChanged(prevIndex + 1)
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
                    snapshot?.let {
                        val post = snapshot.getValue(Post::class.java)

                        post?.let {
                            val existIndex = posts.map { it.postId }.indexOf(post.postId)
                            posts.removeAt(existIndex)
                            applicationRecycler.adapter?.notifyItemRemoved(existIndex)

                            if (prevChildKey == null) {
                                posts.add(post)
                                applicationRecycler.adapter?.notifyItemChanged(posts.size - 1)
                            } else {
                                val prevIndex = posts.map { it.postId }.indexOf(prevChildKey)
                                posts.add(prevIndex + 1, post)
                                applicationRecycler.adapter?.notifyItemChanged(prevIndex + 1)
                            }
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    snapshot?.let {
                        val post = snapshot.getValue(Post::class.java)
                        post?.let { post ->
                            val existIndex = posts.map { it.postId }.indexOf(post.postId)
                            posts.removeAt(existIndex)
                            applicationRecycler.adapter?.notifyItemRemoved(existIndex)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error?.toException()?.printStackTrace()
                }
            })

    }

    private fun revokeAccess() {
        firebaseAuth.getCurrentUser()?.delete()
    }

    private fun readData(uid : String){
        database = FirebaseDatabase.getInstance().getReference("/Users/Influencers")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val snsid = it.child("sns").value
                val name = it.child("username").value
                val imageuri = it.child("image").value
                username = name.toString()
                binding.influencerName.text = name.toString()
                binding.influencersnsid.text = snsid.toString()
                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.imageView)

            }else{
            }
        }.addOnFailureListener {
            Toast.makeText(this, "read fail", Toast.LENGTH_SHORT).show()
        }

    }

    inner class InfluencerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff

    }

    inner class InfluencerAdapter: RecyclerView.Adapter<InfluencerViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfluencerViewHolder {
            return InfluencerViewHolder(LayoutInflater.from(this@MyPageActivity).inflate(R.layout.posts,parent,false))
        }

        override fun getItemCount(): Int {
            return posts.size
        }

        override fun onBindViewHolder(holder: InfluencerViewHolder, position: Int) {
            val post = posts[position]
            postlists = posts
            Picasso.get().load(Uri.parse(post.image)).fit().centerCrop().into(holder.imageView)
            holder.contentsText.text = post.productname
            holder.timeTextView.text= getDiffTimeText(post.writeTime as Long)

            holder.itemView.setOnClickListener {
                val postId = post.postId.toString()
                val mDialogView = LayoutInflater.from(this@MyPageActivity).inflate(R.layout.dialog_influencer_application, null)
                val mBuilder = AlertDialog.Builder(this@MyPageActivity)
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                val withdrawBtn = mDialogView.findViewById<Button>(R.id.withdrawbtn)
                withdrawBtn.setOnClickListener { // 신청 삭제
                    database = FirebaseDatabase.getInstance().getReference("Posts/${post.postId}/Applications")
                    database.child(uid).removeValue().addOnSuccessListener {
                        Toast.makeText(this@MyPageActivity, "Application is deleted.", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {  }

                    database = FirebaseDatabase.getInstance().getReference("Users/Influencers/$uid/Applications")
                    database.child(post.postId).removeValue().addOnSuccessListener {
                    }.addOnFailureListener {  }

                    mAlertDialog.dismiss()
                }
                // 리뷰 기능

                val reviewBtn = mDialogView.findViewById<Button>(R.id.reviewbtn)
                reviewBtn.visibility = View.GONE
                // 광고주가 해당 인플루언서에 대해 거래 완료를 눌렀으면, 버튼 활성화
                FirebaseDatabase.getInstance().getReference("/Posts/$postId/Transaction").get().addOnSuccessListener {
                    val key = it.child("$uid").value
                    if(key.toString() == "yes"){
                        reviewBtn.visibility = View.VISIBLE
                    }
                }
                reviewBtn.setOnClickListener { // 리뷰 기능 있는 activity
                    database = FirebaseDatabase.getInstance().getReference("/Posts/$postId/Reviews")     // 이미 작성한 리뷰가 존재하면 -> 리뷰 수정 불가능하게..!
                    database.child(uid).get().addOnSuccessListener {
                        if (it.exists()) {
                            val readuid = it.child("uid").value
                            if (readuid.toString() == uid) {
                                review = true
                                if(review){
                                    Toast.makeText(this@MyPageActivity, "You already write the review!", Toast.LENGTH_SHORT).show()
                                }else{}
                            }
                        }
                        else{
                            val intent = Intent(this@MyPageActivity, ReviewActivity::class.java)
                            intent.putExtra("postId", postId)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            finish()
                        }
                    }.addOnFailureListener {
                    }
                    mAlertDialog.dismiss()
                }
                val reviewListBtn = mDialogView.findViewById<Button>(R.id.reviewlist)
                reviewListBtn.setOnClickListener {
                    val intent = Intent(this@MyPageActivity, ReviewListActivity::class.java)
                    intent.putExtra("postId",postId)
                    startActivity(intent)
                    mAlertDialog.dismiss()
                }
                }

            }


        }

        fun getDiffTimeText(targetTime: Long) : String{
            val curDateTime = DateTime()
            val targetDateTime = DateTime().withMillis(targetTime)
            val diffDay = Days.daysBetween(curDateTime, targetDateTime).days
            val diffHours = Hours.hoursBetween(targetDateTime, curDateTime).hours
            val diffMinutes = Minutes.minutesBetween(targetDateTime, curDateTime).minutes

            if(diffDay == 0){
                if(diffHours == 0 && diffMinutes == 0){
                    return "Just before"
                }
                return if (diffHours > 0){
                    if(diffHours == 1){
                        " "+ diffHours + "hour ago"
                    }
                    else{"" + diffHours + "hours ago"}
                }else{
                    if(diffMinutes == 1) {
                        ""+diffMinutes+"minutes ago"
                    }
                    else{
                        ""+diffMinutes+"minutes ago"
                    }
                }
            }else{
                val format = SimpleDateFormat("yyyy.MM.DD.HH")
                return format.format(Date(targetTime))
            }

        }


    }



