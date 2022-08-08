package com.example.trello.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager.LayoutParams.*
import android.widget.TextView
import com.example.trello.activities.IntroActivity
import com.example.trello.R

class SplashActivity : AppCompatActivity() {
    lateinit var tv_app_name: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        tv_app_name = findViewById(R.id.tv_app_name)

        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )

        val typeface: Typeface = Typeface.createFromAsset(assets, "Penthool.ttf")

        tv_app_name.typeface = typeface

        Handler().postDelayed({
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        }, 500)
    }
}