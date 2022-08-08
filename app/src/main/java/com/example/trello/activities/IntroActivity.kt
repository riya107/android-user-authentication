package com.example.trello.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.Button
import android.widget.TextView
import com.example.trello.R
import com.example.trello.activities.SignupActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class IntroActivity : BaseActivity() {
    lateinit var tv_intro_title: TextView
    lateinit var tv_let: TextView
    lateinit var btn_up: Button
    lateinit var btn_in: Button
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContentView(R.layout.activity_intro)
        tv_intro_title = findViewById(R.id.tv_intro_title)
        tv_let = findViewById(R.id.tv_let)
        btn_up = findViewById(R.id.btn_up)
        btn_in = findViewById(R.id.btn_in)
        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )


        btn_up.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        btn_in.setOnClickListener {
            startActivity(Intent(this, SigninActivity::class.java))
        }

        val typeface: Typeface = Typeface.createFromAsset(assets, "Penthool.ttf")

        tv_intro_title.typeface = typeface
        tv_let.typeface = typeface

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        onBack()
    }
}