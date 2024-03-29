package com.example.everyones_sponsorship.advertiser

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.*
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.chat.ChatListActivity
import com.example.everyones_sponsorship.databinding.ActivityAdvertiserMainBinding
import com.example.everyones_sponsorship.start.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_photo.view.*
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.activity_profile_details.view.*
import kotlinx.android.synthetic.main.fragment_mypage.view.*
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*
import org.joda.time.DateTime


class AdvertiserMainActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityAdvertiserMainBinding
    val posts : MutableList<Post> = mutableListOf()
    private lateinit var database : DatabaseReference
    val userId =  FirebaseAuth.getInstance().currentUser!!.uid

    private fun revokeAccess() {
        FirebaseAuth.getInstance().getCurrentUser()?.delete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdvertiserMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        readData(userId)

        setSupportActionBar(binding.toolbar)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        binding.uploadproductbtn.setOnClickListener {
            val intent = Intent(this, AddPhotoActivity::class.java)
            startActivity(intent)
        }
        binding.advertiserLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this@AdvertiserMainActivity, "Successfully log-out", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.editAdvertiser.setOnClickListener{
            val intent = Intent(this, EditprofileAdvertiser::class.java)
            startActivity(intent)
        }

        binding.withdrawl.setOnClickListener {
            val mDialogView = LayoutInflater.from(this@AdvertiserMainActivity).inflate(R.layout.dialog_withdrawl_check, null)
            val mBuilder = AlertDialog.Builder(this@AdvertiserMainActivity)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()

            val nobtn = mDialogView.findViewById<Button>(R.id.no)
            nobtn.setOnClickListener {
                mAlertDialog.dismiss()
            }
            val yesbtn = mDialogView.findViewById<Button>(R.id.yes)

            yesbtn.setOnClickListener {

                database = FirebaseDatabase.getInstance().getReference("Users")
                database.child("Advertisers/$userId").removeValue().addOnSuccessListener{}.addOnFailureListener{}

                FirebaseDatabase.getInstance().getReference("Posts").orderByChild("writeId").equalTo(userId).get().addOnSuccessListener {
                    for (i in posts) {
                        val post = i
                        val postId = post.postId
                        FirebaseDatabase.getInstance().getReference("Posts").child("$postId").removeValue()
                    }
                }

                revokeAccess()
                mAlertDialog.dismiss()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()

            }
        }

    val layoutManager = LinearLayoutManager(this@AdvertiserMainActivity)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recycler_application.layoutManager = layoutManager
        recycler_application.adapter = AdvertiserAdapter()


// 여기서 자기가 올린거에 해당하는 거만 불러오게 만들기!! ( 지금 이거는 influencer에 똑같이 적용하면 될듯)

        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("writeId").equalTo(userId).addChildEventListener(object:
            ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
            snapshot?.let{
                snapshot ->
                val post = snapshot.getValue(Post::class.java)
                post?.let{
                    if(prevChildKey == null){
                        posts.add(it)
                        recycler_application.adapter?.notifyItemInserted(posts.size-1)
                    }else{
                        val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                        posts.add(prevIndex+1, post)
                        recycler_application.adapter?.notifyItemInserted(prevIndex+1)
                }

                }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, prevChildKey: String?) {
            snapshot?.let{
                val post = snapshot.getValue(Post::class.java)
                post?.let{
                    val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                    posts[prevIndex + 1] = post
                    recycler_application.adapter?.notifyItemChanged(prevIndex+1)
                }
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
            snapshot?.let{
                val post = snapshot.getValue(Post::class.java)

                post?.let{
                    val existIndex = posts.map{it.postId}.indexOf(post.postId)
                    posts.removeAt(existIndex)
                    recycler_application.adapter?.notifyItemRemoved(existIndex)

                    if(prevChildKey == null){
                        posts.add(post)
                        recycler_application.adapter?.notifyItemChanged(posts.size-1)
                    }else{
                        val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                        posts.add(prevIndex+1,post)
                        recycler_application.adapter?.notifyItemChanged(prevIndex+1)
                    }
                }
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            snapshot?.let{
                val post = snapshot.getValue(Post::class.java)
                post?.let{post->
                    val existIndex = posts.map{it.postId}.indexOf(post.postId)
                    posts.removeAt(existIndex)
                    recycler_application.adapter?.notifyItemRemoved(existIndex)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            error?.toException()?.printStackTrace()
        }
    })

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemview = item.itemId

        // for extension (search influencer) -> use when
        when(itemview){
            R.id.admenuchat -> {
                val intent = Intent(this, ChatListActivity::class.java)
                intent.putExtra("whoami","Advertisers")
                startActivity(intent)
            }
        }
        return false
    }

    inner class AdvertiserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff

    }

    inner class AdvertiserAdapter: RecyclerView.Adapter<AdvertiserViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertiserViewHolder {
            return AdvertiserViewHolder(LayoutInflater.from(this@AdvertiserMainActivity).inflate(R.layout.posts,parent,false))
        }

        override fun getItemCount(): Int {
            return posts.size
        }

        override fun onBindViewHolder(holder: AdvertiserViewHolder, position: Int) {
            val post = posts[position]
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage!!.reference
            var imageFileName = "IMAGE_" + post.postId + "_.png"
            storageRef!!.child("images").child(imageFileName)?.downloadUrl?.addOnSuccessListener { uri->
                Picasso.get().load(uri).fit().centerCrop().into(holder.imageView)
            }
            holder.contentsText.text = post.productname
            holder.timeTextView.text= getDiffTimeText(post.writeTime as Long)

            holder.itemView.setOnClickListener {
                val mDialogView = LayoutInflater.from(this@AdvertiserMainActivity).inflate(R.layout.dialog_advertiser_product, null)
                val mBuilder = AlertDialog.Builder(this@AdvertiserMainActivity)
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                //  edit button 누르면 수정 페이지로
                val editButton = mDialogView.findViewById<Button>(R.id.editButton)
                editButton.setOnClickListener {
                    val intent = Intent(this@AdvertiserMainActivity, EditActivity::class.java)
                    intent.putExtra("postId",post.postId)
                    intent.putExtra("productname",post.productname)
                    intent.putExtra("message",post.message)
                    intent.putExtra("category",post.category)
                    intent.putExtra("rating",post.rating)
                    intent.putExtra("image",post.image)
                    startActivity(intent)
                    mAlertDialog.dismiss()
                }
                //  application list 버튼 누르면 인플루언서 신청 목록 보이게
                val applicationBtn = mDialogView.findViewById<Button>(R.id.applicationlistbtn)
                applicationBtn.setOnClickListener {
                    val intent = Intent(this@AdvertiserMainActivity, AdvertiserApplicationActivity::class.java)
                    intent.putExtra("postId",post.postId)
                    startActivity(intent)
                    mAlertDialog.dismiss()
                }
                // 삭제 버튼 -> 재확인 dialog 띄움 -> no하면 메인 페이지 보이게, yes -> firebase에서 데이터 삭제
                val deleteButton = mDialogView.findViewById<Button>(R.id.deleteButton)
                deleteButton.setOnClickListener {
                    mAlertDialog.dismiss()
                    val deletedialog = LayoutInflater.from(this@AdvertiserMainActivity).inflate(R.layout.dialog_delete_check, null)
                    val deleteBuilder = AlertDialog.Builder(this@AdvertiserMainActivity)
                        .setView(deletedialog)
                    val deleteDialog = deleteBuilder.show()
                    val noBtn = deletedialog.findViewById<Button>(R.id.no)
                    noBtn.setOnClickListener {
                        deleteDialog.dismiss()
                    }
                    val yesBtn = deletedialog.findViewById<Button>(R.id.yes)
                    yesBtn.setOnClickListener {
                        database = FirebaseDatabase.getInstance().getReference("Posts")
                        database.child(post.postId).removeValue().addOnSuccessListener {
                            Toast.makeText(this@AdvertiserMainActivity, "Post is removed.", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {  }
                        deleteDialog.dismiss()

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
                val format = SimpleDateFormat("yyyy-MM-DD")
                return format.format(Date(targetTime))
            }

        }
    }
    private fun readData(uid : String){
        database = FirebaseDatabase.getInstance().getReference("/Users/Advertisers")
        database.child(uid).get().addOnSuccessListener {
            if(it.exists()){
                val name = it.child("username").value
                val business = it.child("business").value
                val imageuri = it.child("image").value
                val rating = it.child("rating").value
                binding.advertiserName.text = name.toString()
                binding.advertiserid.text = business.toString()
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage!!.reference
                var imageFileName = "IMAGE_" + uid + "_.png"
                storageRef!!.child("images").child(imageFileName)?.downloadUrl?.addOnSuccessListener { uri->
                    Picasso.get().load(uri).fit().centerCrop().into(binding.imageView)
                }
//                Picasso.get().load(Uri.parse(imageuri.toString())).fit().centerCrop().into(binding.imageView)



            }else{
            }
        }.addOnFailureListener {
            Toast.makeText(this@AdvertiserMainActivity, "read fail", Toast.LENGTH_SHORT).show()
        }

    }
}