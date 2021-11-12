package com.example.everyones_sponsorship

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.everyones_sponsorship.databinding.ActivityInfluencermainBinding


class InfluencerMainActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityInfluencermainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfluencermainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 네비게이션들을 담는 호스트
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frame) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바텀 네비게이션 뷰 와 네비게이션을 묶어준다
        NavigationUI.setupWithNavController(binding.bottomNavi, navController)


    }
}