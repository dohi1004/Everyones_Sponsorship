package com.example.everyones_sponsorship

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.everyones_sponsorship.databinding.ActivityCategorydetailsBinding
import com.example.everyones_sponsorship.databinding.ActivityInfluencermainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_categorydetails.*
import kotlinx.android.synthetic.main.activity_influencermain.*
import kotlinx.android.synthetic.main.posts.view.*
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import java.text.SimpleDateFormat
import java.util.*
// -> recycler view item 하나 선택했을 때 아이템 정보가 로드 안됨 -> 확인 필요
// category 별로 보이게 만들기
class CategoryDetailsActivity : AppCompatActivity() {
    private lateinit var  binding : ActivityCategorydetailsBinding
    val posts : MutableList<Post> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorydetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // recycler view 구현 -> 특정 category에만 있는 list를 반환하도록 짜기 ( firebase 부분)
        // search result -> 총 몇개의 게시물 있는 지 나타내기

        val category = intent.getStringExtra("category")
        val color = "#7C777B"
        binding.categoryname.text = category
        if(category == "Pet"){
            binding.categoryimage.setImageResource(R.drawable.ic_pet_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Clothes"){
            binding.categoryimage.setImageResource(R.drawable.ic_clothes_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Sports"){
            binding.categoryimage.setImageResource(R.drawable.ic_sports_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Furniture"){
            binding.categoryimage.setImageResource(R.drawable.ic_chair_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Food"){
            binding.categoryimage.setImageResource(R.drawable.ic_food_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Book"){
            binding.categoryimage.setImageResource(R.drawable.ic_book_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Game"){
            binding.categoryimage.setImageResource(R.drawable.ic_gamepad_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Device"){
            binding.categoryimage.setImageResource(R.drawable.ic_device_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }
        if(category == "Beauty"){
            binding.categoryimage.setImageResource(R.drawable.ic_air_freshener_solid)
            binding.categoryimage.setColorFilter(Color.parseColor(color))
        }



        val layoutManager = LinearLayoutManager(this)

        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        categoryrecyclerview.layoutManager = layoutManager
        categoryrecyclerview.adapter = InfluencerAdapter()

        FirebaseDatabase.getInstance().getReference("/Posts").orderByChild("writeTime").addChildEventListener(object:
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                        snapshot ->
                    val post = snapshot.getValue(Post::class.java)
                    post?.let{
                        if(prevChildKey == null){
                            posts.add(it)
                            categoryrecyclerview.adapter?.notifyItemInserted(posts.size-1)
                        }else{
                            val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                            posts.add(prevIndex+1, post)
                            categoryrecyclerview.adapter?.notifyItemInserted(prevIndex+1)
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
                        categoryrecyclerview.adapter?.notifyItemChanged(prevIndex+1)
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {
                snapshot?.let{
                    val post = snapshot.getValue(Post::class.java)

                    post?.let{
                        val existIndex = posts.map{it.postId}.indexOf(post.postId)
                        posts.removeAt(existIndex)
                        categoryrecyclerview.adapter?.notifyItemRemoved(existIndex)

                        if(prevChildKey == null){
                            posts.add(post)
                            categoryrecyclerview.adapter?.notifyItemChanged(posts.size-1)
                        }else{
                            val prevIndex = posts.map{it.postId}.indexOf(prevChildKey)
                            posts.add(prevIndex+1,post)
                            categoryrecyclerview.adapter?.notifyItemChanged(prevIndex+1)
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
                        categoryrecyclerview.adapter?.notifyItemRemoved(existIndex)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error?.toException()?.printStackTrace()
            }
        })


    }

    inner class InfluencerViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.productimage
        val contentsText : TextView = itemView.productname
        val timeTextView : TextView = itemView.timediff
        val who : TextView = itemView.writers

    }

    inner class InfluencerAdapter: RecyclerView.Adapter<InfluencerViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfluencerViewHolder {
            return InfluencerViewHolder(LayoutInflater.from(this@CategoryDetailsActivity).inflate(R.layout.posts,parent,false))
        }

        override fun getItemCount(): Int {
            return posts.size
        }

        override fun onBindViewHolder(holder: InfluencerViewHolder, position: Int) {
            val post = posts[position]
            Picasso.get().load(Uri.parse(post.image)).fit().centerCrop().into(holder.imageView)
            holder.contentsText.text = post.productname
            holder.who.text = post.postId
            holder.timeTextView.text= getDiffTimeText(post.writeTime as Long)
            // recylcer view item 클릭 시
            holder.itemView.setOnClickListener {
                intent.putExtra("postId",post.postId)
                val intent = Intent(this@CategoryDetailsActivity, ProductDetailsActivity::class.java)
                startActivity(intent)
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
}