package com.example.trello.activities

import android.os.Bundle
import android.os.Handler
import android.view.WindowManager.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.trello.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SigninActivity : BaseActivity() {
    var toast: Toast? = null
    lateinit var bkin: ImageView
    lateinit var signin: Button
    lateinit var signin_email: EditText
    lateinit var signin_password: EditText
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContentView(R.layout.activity_signin)

        bkin = findViewById(R.id.bkin)

        signin = findViewById(R.id.signin)
        signin_email = findViewById(R.id.signin_email)
        signin_password = findViewById(R.id.signin_password)

        signin.setOnClickListener {
            val email = signin_email.text.trim().toString()
            val password = signin_password.text.toString()

            if (validateForm(email, password)) {
                showDialog()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            hideDialog()
                            val user = auth.currentUser
                            showSnackBar("Authentication successful", R.color.success)
                            Handler().postDelayed({
                                onBackPressed()
                                finish()
                            }, 1000)
                        } else {
                            hideDialog()
                            showSnackBar("Authentication failed", R.color.danger)
                        }
                    }
            }
        }

        window.setFlags(
            LayoutParams.FLAG_FULLSCREEN,
            LayoutParams.FLAG_FULLSCREEN
        )

        bkin.setOnClickListener {
            onBackPressed()
        }
    }

    fun validateForm(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                showSnackBar("Please enter email", R.color.danger)
                false
            }
            password.isEmpty() -> {
                showSnackBar("Please enter password", R.color.danger)
                false
            }
            else -> {
                true
            }
        }
    }
}