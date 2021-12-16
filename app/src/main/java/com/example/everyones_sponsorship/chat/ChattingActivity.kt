package com.example.everyones_sponsorship.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.everyones_sponsorship.Friend
import com.example.everyones_sponsorship.FriendAdvertisers
import com.example.everyones_sponsorship.R
import com.example.everyones_sponsorship.Review
import com.example.everyones_sponsorship.advertiser.AdvertiserMainActivity
import com.example.everyones_sponsorship.databinding.ActivityChattingBinding
import com.example.everyones_sponsorship.influencer.SearchresultActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_search.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatModel (val users: HashMap<String, Boolean> = HashMap(),
                 val comments : HashMap<String, Comment> = HashMap(),
                 val postID : HashMap<String, Boolean> = HashMap()
){
    class Comment(val uid: String? = null, val message: String? = null, val time: String? = null)
}

class ChattingActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance().getReference().push()
    private val database = Firebase.database
    private val databaseReference = database.getReference()
    //    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomUid : String? = null
    private var destinationUid : String? = null
    private var uid : String? = null
    private var recyclerView : RecyclerView? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageView = findViewById<Button>(R.id.Button_send)
        val editText = findViewById<TextView>(R.id.EditText_chat)
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()
        val whoami = intent.getStringExtra("whoami")
        var postId = intent.getStringExtra("postId")


        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.my_recycler_view)
        //destinationUid = "minseon"
        destinationUid = intent.getStringExtra("destinationUid")

//        postId = postId?.split("[").toString()


        if(whoami == "Influencers"){
            binding.transaction.visibility = View.INVISIBLE
        }
        if(whoami == "Advertisers"){
            FirebaseDatabase.getInstance().getReference("/Users/Influencers/$destinationUid/Reviews").child("$postId").get().addOnSuccessListener {
                if(it.exists()){
                    binding.transaction.visibility = View.INVISIBLE
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Transaction fail", Toast.LENGTH_SHORT).show()
            }

        }


        binding.transaction.setOnClickListener {
            // 거래완료 버튼 advertiser가 클릭 시 -> transaction = true로 만들기
            if(whoami == "Advertisers"){
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ratingforinfluencer, null)
                val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                val applyBtn = mDialogView.findViewById<Button>(R.id.applybtn)
                val BackBtn = mDialogView.findViewById<Button>(R.id.backbtn)
                val rating = mDialogView.findViewById<RatingBar>(R.id.rating)
                rating.rating = 0.0F
                applyBtn.setOnClickListener {
                    FirebaseDatabase.getInstance().getReference("/Posts/$postId").child("Transaction/$destinationUid").setValue("yes").addOnSuccessListener {
                        Toast.makeText(this, "Transaction complete", Toast.LENGTH_SHORT).show()
                        binding.transaction.visibility = View.INVISIBLE
                    }.addOnFailureListener {
                        Toast.makeText(this, "Transaction fail", Toast.LENGTH_SHORT).show()
                    }
                    val currentrating = rating.rating
                    val reviewtxt = mDialogView.findViewById<EditText>(R.id.reviewforinfluencer)
                    val reviewtext = reviewtxt.text.toString()
                    val review = Review(rating = currentrating.toString(), text = reviewtext, username = uid, uid = postId)
                    // 리뷰 디비에 추가하기
                    FirebaseDatabase.getInstance().getReference("/Users/Influencers/$destinationUid/Reviews").child(
                        postId!!
                    ).setValue(review).addOnSuccessListener {
                    }.addOnFailureListener {
                        Toast.makeText(this, "Review fail", Toast.LENGTH_SHORT).show()
                    }

                    val ref = FirebaseDatabase.getInstance().getReference("Users/Influencers/$destinationUid/Reviews")
                    var average = 0.0
                    var totalrating = 0.0
                    var n = 0
                    ref.get().addOnSuccessListener {
                        it.children.forEach(){
                            totalrating += it.child("rating").value.toString().toFloat()
                            n += 1
                        }
                        if(n!=0){
                            average = totalrating/n
                        }

                        // 인플루언서 rating 업데이트
                        FirebaseDatabase.getInstance().getReference("Users/Influencers/$destinationUid/rating").setValue(average).addOnSuccessListener {
                        }.addOnFailureListener{}

                        // application 부분도 rating 업데이트
                        databaseReference.child("Posts").orderByChild("Applications/$destinationUid/uid").equalTo("$destinationUid")
                            .get().addOnSuccessListener {
                                it.children.forEach(){
                                    it.child("Applications/$destinationUid/rating").ref.setValue(average)
                                }
                            }.addOnFailureListener {
                                print("안됨")
                            }

                    }.addOnFailureListener {  }
                    mAlertDialog.dismiss()
                }
                BackBtn.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
        }



        imageView.setOnClickListener{
            val chatModel = ChatModel()
            chatModel.users.put(uid.toString(), true)
            chatModel.users.put(destinationUid!!, true)
            chatModel.postID.put(postId.toString(), true)
            val comment = ChatModel.Comment(uid, editText.text.toString(), curTime)

            if(chatRoomUid == null){
                imageView.isEnabled = false
                databaseReference.child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        println(chatRoomUid)
                        databaseReference.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                        editText.text = null
                    }, 1000L)
                    Log.d("chatUidNull dest", "$destinationUid")
                }
            }else{
                databaseReference.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                editText.text = null
                Log.d("chatUidNotNull dest", "$destinationUid")
            }
        }
        checkChatRoom()
    }
    private fun checkChatRoom(){
        val imageView = findViewById<Button>(R.id.Button_send)
        databaseReference.child("chatrooms").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children){
                        println(item)
                        val chatModel = item.getValue<ChatModel>()
                        if(chatModel?.users!!.containsKey(destinationUid)){
                            chatRoomUid = item.key
                            imageView.isEnabled = true
                            recyclerView?.layoutManager = LinearLayoutManager(this@ChattingActivity)
                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {
        val textView_topName = findViewById<TextView>(R.id.minsun)
        private val comments = ArrayList<ChatModel.Comment>()
        private var friend : Friend? = null // 이부분 influencer나 advertiser dataclass로 연동 필요함
        private var friendadv : FriendAdvertisers? = null
        private var strstr : String? = null
        init{
            var identity: String? = null
            if (getIntent().getStringExtra("whoami")=="Advertisers")  identity="Influencers" else identity="Advertisers"
            databaseReference.child("Users").child("$identity").child(destinationUid.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (identity=="Influencers") {
                        strstr = snapshot.getValue<Friend>()?.username
                    } else {
                        strstr = snapshot.getValue<FriendAdvertisers>()?.username
                    }
                    textView_topName.text = strstr
                    getMessageList()
                }
            })
        }

        fun getMessageList(){
            databaseReference.child("chatrooms").child(chatRoomUid.toString()).child("comments").addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for(data in snapshot.children){
                        val item = data.getValue<ChatModel.Comment>()
                        comments.add(item!!)
                        println(comments)
                    }
                    notifyDataSetChanged()
                    //메세지를 보낼 시 화면을 맨 밑으로 내림
                    recyclerView?.scrollToPosition(comments.size - 1)
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.textView_message.textSize = 20F
            holder.textView_message.text = comments[position].message
            holder.textView_time.text = comments[position].time
            if(comments[position].uid.equals(uid)){ // 본인 채팅
                //holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                holder.textView_name.visibility = View.INVISIBLE
                holder.layout_destination.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            }else{ // 상대방 채팅
                Glide.with(holder.itemView.context)
                    .load(friend?.image)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.imageView_profile)
                holder.textView_name.text = friend?.username
                holder.layout_destination.visibility = View.VISIBLE
                holder.textView_name.visibility = View.VISIBLE
                //holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                holder.layout_main.gravity = Gravity.LEFT
            }
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
            val layout_destination: LinearLayout = view.findViewById(R.id.messageItem_layout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val textView_time : TextView = view.findViewById(R.id.messageItem_textView_time)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }
}