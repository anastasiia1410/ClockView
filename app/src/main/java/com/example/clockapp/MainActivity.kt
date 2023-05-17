package com.example.clockapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.clockapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val myFragment = MainFragment()
        supportFragmentManager.beginTransaction().add(binding.fcMain.id, myFragment)
            .commit()
    }
}