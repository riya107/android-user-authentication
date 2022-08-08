package com.example.trello.firebase

import com.example.trello.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserDao {
    val db=Firebase.firestore
    val usersCollection=db.collection("users")

    fun addUser(user: User){
            GlobalScope.launch(Dispatchers.IO) {
                usersCollection.document(user.id).set(user)
            }
    }

    fun updateUser(id:String,data:HashMap<String,String>){
        GlobalScope.launch(Dispatchers.IO){
            usersCollection.document(id).set(data, SetOptions.merge())
        }
    }

    fun getUser(id:String): Task<DocumentSnapshot> {
            val user = usersCollection.document(id).get()
            return user
    }
}