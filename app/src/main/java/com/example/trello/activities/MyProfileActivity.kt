package com.example.trello.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.webkit.PermissionRequest
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.trello.R
import com.example.trello.firebase.UserDao
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyProfileActivity : BaseActivity() {
    lateinit var update_image:ImageView
    lateinit var update_name:EditText
    lateinit var update_email:EditText
    lateinit var update_phone:EditText
    var PICK_CODE=101
    lateinit var imageURL:String
    lateinit var btn_update:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        update_name = findViewById(R.id.update_name)
        update_email = findViewById(R.id.update_email)
        btn_update = findViewById(R.id.btn_update)
        update_phone = findViewById(R.id.update_phone)

        update_image = findViewById(R.id.update_image)

        var intent: Intent = intent

        var name = intent.getStringExtra("name")
        var email = intent.getStringExtra("email")
        var image = intent.getStringExtra("image")

        imageURL=image?:""

        update_name.setText(name)
        update_email.setText(email)

            Glide
                .with(this@MyProfileActivity)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.prof)
                .error(R.drawable.prof)
                .into(update_image);

        btn_update.setOnClickListener {
            if(imageURL==null){
                showSnackBar("Please upload image",R.color.danger)
            }
            if (update_name.text.isEmpty()) {
                showSnackBar("Please enter name", R.color.danger)
            } else if (update_email.text.isEmpty()) {
                showSnackBar("Please enter email", R.color.danger)
            }
            else{
                var intent: Intent = intent
                val id=intent.getStringExtra("id")
                val userDao=UserDao()
                showDialog()
                userDao.updateUser(id!!, hashMapOf("image" to imageURL,"name" to update_name.text.toString(),"email" to update_email.text.toString(),"mobile" to update_phone.text.toString()))
                hideDialog()
                showSnackBar("User Updated Successfully",R.color.success)

                Handler().postDelayed({
                    onBackPressed()
                },2000)
            }
        }

            update_image.setOnClickListener {
                Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                            var galleryIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )

                            startActivityForResult(galleryIntent, PICK_CODE)
                        }

                        override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: com.karumi.dexter.listener.PermissionRequest?,
                            p1: PermissionToken?
                        ) {
                            p1?.continuePermissionRequest()
                        }
                    }).check()
            }

            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )

            setUpActionBar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.data?.let{
            if(requestCode==PICK_CODE && resultCode== Activity.RESULT_OK){
                showDialog()

                GlobalScope.launch(Dispatchers.IO) {
                    val storage = Firebase.storage
                    val storageRef = storage.reference
                    val imagesRef=storageRef.child("images/users")
                    val usersRef = imagesRef.child("USER_IMAGE_"+System.currentTimeMillis()+"."+getMimeType(it))
                    val uploadTask = usersRef.putFile(it)

                    uploadTask.addOnSuccessListener {taskSnapshot->
                        taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {uri->
                            imageURL=uri.toString()
                            update_image.setImageURI(it)
                            hideDialog()
                        }?.addOnFailureListener {
                            showSnackBar("Image Upload Failed",R.color.danger)
                            hideDialog()
                        }
                    }.addOnFailureListener{
                        showSnackBar("Image Upload Failed",R.color.danger)
                        hideDialog()
                    }
                }
            }
        }
    }
        fun getMimeType(uri:Uri):String?{
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
        }

    fun setUpActionBar() {
        val toolBar: Toolbar = findViewById(R.id.profile_toolbar)
        setSupportActionBar(toolBar)

        val actionBar=supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)

        actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_white_ios_24)

        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}