package com.github.loadpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.loadpager.databinding.ActivityMainBinding
import com.github.loadpager.sample.SampleActivity

/**
 * 主Activity
 * 作为应用程序的入口点，提供进入各种Sample的入口
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setupClickListeners()
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "LoadPager示例"
        }
    }

    /**
     * 设置点击事件监听
     */
    private fun setupClickListeners() {
        binding.btnListSample.setOnClickListener {
            SampleActivity.start(this)
        }
    }
}