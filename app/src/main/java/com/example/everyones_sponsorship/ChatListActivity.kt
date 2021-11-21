package com.example.everyones_sponsorship

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.everyones_sponsorship.databinding.ActivityChatlistBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatListActivity : AppCompatActivity() {
    private val fireDatabase = FirebaseDatabase.getInstance().reference

//    inner class ChatModel (val users: HashMap<String, Boolean> = HashMap(),
//                           val comments : HashMap<String, Comment> = HashMap()){
//        inner class Comment(val uid: String? = null, val message: String? = null, val time: String? = null)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = findViewById<RecyclerView>(R.id.chatfragment_recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
        recyclerView.adapter = RecyclerViewAdapter()
//        val intent = Intent(this, ChattingActivity::class.java)
//        intent.putExtra("destinationUid", "InfluencerName")
//        startActivity(intent)
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        private val chatModel = ArrayList<ChatModel>()
        private var uid : String? = null
        private val destinationUsers : ArrayList<String> = arrayListOf()

        init {
            uid = Firebase.auth.currentUser?.uid.toString()
            println(uid)

            fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatModel.clear()
                    for(data in snapshot.children){
                        chatModel.add(data.getValue<ChatModel>()!!)
                        println(data)
                    }
                    notifyDataSetChanged()
                }
            })
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {


            return CustomViewHolder(LayoutInflater.from(this@ChatListActivity).inflate(R.layout.item_chat, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.chat_item_imageview)
            val textView_title : TextView = itemView.findViewById(R.id.chat_textview_title)
            val textView_lastMessage : TextView = itemView.findViewById(R.id.chat_item_textview_lastmessage)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            var destinationUid: String? = null
            //채팅방에 있는 유저 모두 체크
            for (user in chatModel[position].users.keys) {
                if (!user.equals(uid)) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }

            var identity: String? = null
            if (getIntent().getStringExtra("whoami")=="Advertisers")  identity = "Influencers" else identity = "Advertisers"
            fireDatabase.child("Users").child("$identity").child("$destinationUid").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var strstr : String? = null
                    var strimage : String? = null
                    if (identity=="Influencers") {
                        strstr = snapshot.getValue<Friend>()?.username
                        strimage = snapshot.getValue<Friend>()?.image
                    } else {
                        strstr = snapshot.getValue<FriendAdvertisers>()?.username
                        strimage = snapshot.getValue<FriendAdvertisers>()?.image
                    }
                    Glide.with(holder.itemView.context).load(strimage) // friend?.image
                        .apply(RequestOptions().circleCrop())
                        .into(holder.imageView)
                    holder.textView_title.text = strstr
                }
            })
            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져
            val commentMap = TreeMap<String, ChatModel.Comment>(reverseOrder())
            commentMap.putAll(chatModel[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.textView_lastMessage.text = chatModel[position].comments[lastMessageKey]?.message

            //채팅창 선책 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(this@ChatListActivity, ChattingActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return chatModel.size
        }
    }

}