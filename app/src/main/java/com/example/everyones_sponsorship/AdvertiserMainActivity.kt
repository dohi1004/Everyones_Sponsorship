package com.example.everyones_sponsorship

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.databinding.ActivityAdvertiserMainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_photo.view.*
import kotlinx.android.synthetic.main.activity_advertiser_main.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdvertiserMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        binding.uploadproductbtn.setOnClickListener {
            val intent = Intent(this,AddPhotoActivity::class.java)
            startActivity(intent)
        }

    val layoutManager = LinearLayoutManager(this@AdvertiserMainActivity)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recycler_application.layoutManager = layoutManager
        recycler_application.adapter = AdvertiserAdapter()


// 여기서 자기가 올린거에 해당하는 거만 불러오게 만들기!! ( 지금 이거는 influencer에 똑같이 적용하면 될듯)

    FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("writeTime").addChildEventListener(object: ChildEventListener{
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
                val intent = Intent(this,ChattingActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }

    inner class AdvertiserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff
        val who : TextView = itemView.writers

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
            Picasso.get().load(Uri.parse(post.image)).fit().centerCrop().into(holder.imageView)
            holder.contentsText.text = post.productname
            holder.who.text = post.postId
            holder.timeTextView.text= getDiffTimeText(post.writeTime as Long)

            holder.itemView.setOnClickListener {
                val mDialogView = LayoutInflater.from(this@AdvertiserMainActivity).inflate(R.layout.advertiser_product_dialog, null)
                val mBuilder = AlertDialog.Builder(this@AdvertiserMainActivity)
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                //  edit button 누르면 수정 페이지로
                val editButton = mDialogView.findViewById<Button>(R.id.editButton)
                editButton.setOnClickListener {
                    val intent = Intent(this@AdvertiserMainActivity, EditActivity::class.java)
                    intent.putExtra("postId",post.postId)
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
}