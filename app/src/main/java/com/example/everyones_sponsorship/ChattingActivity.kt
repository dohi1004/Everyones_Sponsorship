package com.example.everyones_sponsorship

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.everyones_sponsorship.databinding.ActivityChattingBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ChatModel (val users: HashMap<String, Boolean> = HashMap(),
                 val comments : HashMap<String, Comment> = HashMap()){
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

        uid = Firebase.auth.currentUser?.uid.toString()
        recyclerView = findViewById(R.id.my_recycler_view)
        //destinationUid = "minseon"
        destinationUid = intent.getStringExtra("destinationUid")

        imageView.setOnClickListener{
            val chatModel = ChatModel()
            chatModel.users.put(uid.toString(), true)
            chatModel.users.put(destinationUid!!, true)
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


//            val name = Firebase.auth.currentUser
//            val userId = name?.uid
//            val userIdSt = userId.toString()
//            val chat = ChatData(binding.EditTextChat.getText().toString(),userIdSt)
//            if (chat != null){
//                databaseReference.child("Chat").child("$userIdSt").push().setValue(chat)
//            }
//            binding.EditTextChat.setText("") // 입력창 초기화
//
//            var mRecyclerView = binding.myRecyclerView
//            mRecyclerView.setHasFixedSize(true)
//            mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
//            //mRecyclerView.adapter =
//            var listChat = arrayListOf<String>()
//            val adapter = ArrayAdapter(this, R.layout.row_chat, listChat)
//
//            databaseReference.addChildEventListener(object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                }
//
//                override fun onChildRemoved(snapshot: DataSnapshot) {
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })
        }
        //checkChatRoom()
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
        init{
            databaseReference.child("users").child(destinationUid.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    friend = snapshot.getValue<Friend>()
                    textView_topName.text = friend?.username
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
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)

            return MessageViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.textView_message.textSize = 20F
            holder.textView_message.text = comments[position].message
            holder.textView_time.text = comments[position].time
            if(comments[position].uid.equals(uid)){ // 본인 채팅
//                holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
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
//                holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
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