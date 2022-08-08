package com.example.trello.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.firebase.UserDao
import com.example.trello.models.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var drawer: DrawerLayout
    lateinit var auth: FirebaseAuth
    lateinit var nav_view: NavigationView
    lateinit var profile_image: ImageView
    lateinit var name: TextView
    lateinit var nav_header: View
    var userName: String?=null
    var userEmail:String?=null
    var userImage:String?=null

    override fun onRestart() {
        super.onRestart()
        val refresh = Intent(this, MainActivity::class.java)
        startActivity(refresh)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContentView(R.layout.activity_main)

        drawer = findViewById(R.id.drawer_layout)

        nav_view = findViewById(R.id.nav_view)

        nav_header = nav_view.inflateHeaderView(R.layout.nav_header)

        name = nav_header.findViewById(R.id.name)

        profile_image=nav_header.findViewById(R.id.profile_image)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setUpActionBar()

        setUpProfile()

        nav_view.setNavigationItemSelectedListener(this)

    }

    fun setUpProfile() {
        val userDao = UserDao()
        auth.currentUser?.uid?.let {
            GlobalScope.launch(Dispatchers.IO) {
                val user = userDao.getUser(it).await().toObject<User>()

                    withContext(Dispatchers.Main){
                        name.text = user?.name
                        userName=user?.name
                        userEmail=user?.email
                        userImage=user?.image

                        Glide
                            .with(this@MainActivity)
                            .load(user?.image)
                            .centerCrop()
                            .placeholder(R.drawable.prof)
                            .error(R.drawable.prof)
                            .into(profile_image);
                    }
                }
            }

        }

    fun setUpActionBar() {
        val toolBar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_baseline_dehaze_24)
        toolBar.setNavigationOnClickListener {
            toggle()
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            onBack()
        }
    }

    fun toggle() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_profile -> {
                var intent = Intent(this,MyProfileActivity::class.java)
                intent.putExtra("name",userName)
                intent.putExtra("email",userEmail)
                intent.putExtra("id",auth.currentUser?.uid)
                intent.putExtra("image",userImage)
                startActivity(intent)
            }
            R.id.menu_signout -> {
                auth.signOut()
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }
        return true
    }
}