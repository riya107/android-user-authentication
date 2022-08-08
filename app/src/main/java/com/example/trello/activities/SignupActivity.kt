package com.example.trello.activities

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.trello.activities.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.example.trello.R
import com.example.trello.firebase.UserDao
import com.example.trello.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupActivity : BaseActivity() {
    var toast: Toast? = null
    lateinit var bkup: ImageView
    lateinit var signup_name: EditText
    lateinit var signup_email: EditText
    lateinit var signup_password: EditText
    lateinit var signup_password_confirm: EditText
    lateinit var signup: Button
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContentView(R.layout.activity_signup)

        bkup = findViewById(R.id.bkup)

        signup_name = findViewById(R.id.signup_name)
        signup_email = findViewById(R.id.signup_email)
        signup_password = findViewById(R.id.signup_password)
        signup_password_confirm = findViewById(R.id.signup_password_confirm)
        signup = findViewById(R.id.signup)

        signup.setOnClickListener {
            val name = signup_name.text.trim().toString()
            val email = signup_email.text.trim().toString()
            val password = signup_password.text.toString()
            val confirm_password = signup_password_confirm.text.toString()

            if (validateForm(name, email, password, confirm_password)) {
                showDialog()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            val userData = User(name = name, id = user!!.uid, email = email)

                            val userDao = UserDao()

                            userDao.addUser(userData)

                            hideDialog()
                            showSnackBar("Authentication Successful", R.color.success)
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
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )

        bkup.setOnClickListener {
            onBackPressed()
        }
    }


    fun validateForm(
        name: String,
        email: String,
        password: String,
        confirm_password: String
    ): Boolean {
        return when {
            name.isEmpty() -> {
                showSnackBar("Please enter name", R.color.danger)
                false
            }
            email.isEmpty() -> {
                showSnackBar("Please enter email", R.color.danger)
                false
            }
            password.isEmpty() -> {
                showSnackBar("Please enter password", R.color.danger)
                false
            }
            confirm_password.isEmpty() -> {
                showSnackBar("Please confirm password", R.color.danger)
                false
            }
            password != confirm_password -> {
                showSnackBar("Password didn't match", R.color.danger)
                false
            }
            else -> {
                true
            }
        }
    }
}