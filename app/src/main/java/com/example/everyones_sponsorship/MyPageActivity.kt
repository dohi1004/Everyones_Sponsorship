package com.example.everyones_sponsorship

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.everyones_sponsorship.databinding.ActivityMypageBinding
import kotlinx.android.synthetic.main.dialog_influencer_profile.*
import kotlinx.android.synthetic.main.dialog_influencer_profile.view.*

class MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.home.setOnClickListener {
            val intent = Intent(this,InfluencerMainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.search.setOnClickListener {
            val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.editbtn.setOnClickListener {
            val mDialogView =
                LayoutInflater.from(this).inflate(R.layout.dialog_influencer_profile, null)
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            val mAlertDialog = mBuilder.show()



            val backButton = mDialogView.findViewById<Button>(R.id.backbtn)
            backButton.setOnClickListener {
                mAlertDialog.dismiss()
            }
            val editButton = mDialogView.findViewById<Button>(R.id.editbtn)
            editButton.setOnClickListener {

            }
        }






    }
}
