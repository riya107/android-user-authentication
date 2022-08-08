package com.example.trello.activities

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.trello.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {
    private lateinit var progress: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showDialog() {
        progress = Dialog(this)

        progress.setContentView(R.layout.progress_bar)

        progress.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        progress.show()
    }

    fun hideDialog() {
        progress.dismiss()
    }

    fun onBack() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Do you want to exit?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            super.onBackPressed()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

    fun showSnackBar(message: String, color: Int) {
        val snackBar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, color))
        snackBar.show()
    }
}