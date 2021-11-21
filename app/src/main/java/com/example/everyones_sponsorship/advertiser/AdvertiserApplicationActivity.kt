package com.example.everyones_sponsorship.advertiser

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.everyones_sponsorship.Influencer
import com.example.everyones_sponsorship.MyAdapter
import com.example.everyones_sponsorship.databinding.ActivityApplicationlistBinding
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_advertiser_main.*
import kotlinx.android.synthetic.main.activity_applicationlist.*
import kotlinx.android.synthetic.main.influencer_info.view.*
import kotlinx.android.synthetic.main.posts.view.*

class AdvertiserApplicationActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    val applications = mutableListOf<Influencer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityApplicationlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val postId = intent.getStringExtra("postId")
        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = MyAdapter(this,applications)

        FirebaseDatabase.getInstance().getReference("/Posts/$postId/Applications").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                        snapshot ->
                    val influencer = snapshot.getValue(Influencer::class.java)
                    influencer?.let{
                        if(prevChildKey == null){
                            applications.add(it)
                            recyclerview.adapter?.notifyItemInserted(applications.size-1)
                        }else{
                            val prevIndex = applications.map{it.uid}.indexOf(prevChildKey)
                            applications.add(prevIndex+1, influencer)
                            recyclerview.adapter?.notifyItemInserted(prevIndex+1)
                        }

                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                    val influencer = snapshot.getValue(Influencer::class.java)
                    influencer?.let{
                        val prevIndex = applications.map{it.uid}.indexOf(prevChildKey)
                        applications[prevIndex + 1] = influencer
                        recyclerview.adapter?.notifyItemChanged(prevIndex+1)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                    val influencer = snapshot.getValue(Influencer::class.java)

                    influencer?.let{
                        val existIndex = applications.map{it.uid}.indexOf(influencer.uid)
                        applications.removeAt(existIndex)
                        recyclerview.adapter?.notifyItemRemoved(existIndex)
                        if(prevChildKey == null){
                            applications.add(influencer)
                            recyclerview.adapter?.notifyItemChanged(applications.size-1)
                        }else{
                            val prevIndex = applications.map{it.uid}.indexOf(prevChildKey)
                            applications.add(prevIndex+1,influencer)
                            recyclerview.adapter?.notifyItemChanged(prevIndex+1)
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                snapshot?.let{
                    val influencer = snapshot.getValue(Influencer::class.java)
                    influencer?.let{post->
                        val existIndex = applications.map{it.uid}.indexOf(post.uid)
                        applications.removeAt(existIndex)
                        recyclerview.adapter?.notifyItemRemoved(existIndex)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error?.toException()?.printStackTrace()
            }
        })

    }




}
