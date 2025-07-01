package com.github.loadpager.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.loadpager.R
import com.github.loadpager.databinding.ActivitySampleBinding

/**
 * Sample演示Activity
 * 展示如何使用pageloadlib库实现列表页面
 */
class SampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        loadSampleFragment(savedInstanceState)
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "LoadPager Sample"
        }
        
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    /**
     * 加载Sample Fragment
     */
    private fun loadSampleFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SampleListFragment.newInstance())
                .commit()
        }
    }

    companion object {
        /**
         * 启动SampleActivity
         * 
         * @param context 上下文
         */
        fun start(context: Context) {
            val intent = Intent(context, SampleActivity::class.java)
            context.startActivity(intent)
        }
    }
} 