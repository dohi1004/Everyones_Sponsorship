package com.example.everyones_sponsorship

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.everyones_sponsorship.databinding.ActivityChattingBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChattingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Firebase.database
        val databaseReference = database.getReference()




//        intent = getIntent()
        val db = FirebaseDatabase.getInstance().getReference().push()
        var CHAT_NAME = db.key.toString()

        binding.ButtonSend.setOnClickListener{
            val chat = ChatData(binding.EditTextChat.getText().toString(),"minseon")
            if (chat != null){
                databaseReference.child("chat").child(CHAT_NAME).push().setValue(chat)
            }
            binding.EditTextChat.setText("") // 입력창 초기화

            var mRecyclerView = binding.myRecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,false)
            //mRecyclerView.adapter =
            var listChat = arrayListOf<String>()
            val adapter = ArrayAdapter(this, R.layout.row_chat, listChat)

            databaseReference.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        }
    }
}