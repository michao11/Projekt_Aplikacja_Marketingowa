package com.example.projekt_aplikacja_marketingowa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FollowsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_follows)

        val followsRecyclerView: RecyclerView = findViewById(R.id.follows_recyclerView)

        auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val follows = mutableListOf<String>()
        val postDetails = mutableListOf<PostDetail>()

        val adapter = PostDetailAdapter(postDetails)
        followsRecyclerView.adapter = adapter
        followsRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("follows")
            .whereEqualTo("FollowUserId", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    follows.add(document.data["PostId"].toString())
                }
                for(postId in follows) {
                }
                for(follow in follows) {
                    val docRef = db.collection("posts").document(follow)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                postDetails.add(
                                    PostDetail(
                                        document.id,
                                        document.data!!["postDetailUserId"].toString(),
                                        document.data!!["postDetailDate"] as com.google.firebase.Timestamp,
                                        document.data!!["postDetailTitle"].toString(),
                                        document.data!!["postDetailPrice"].toString(),
                                        document.data!!["postDetailDescription"].toString(),
                                        document.data!!["postDetailImageUrl"].toString())
                                )
                                adapter.notifyDataSetChanged()
                                Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                            } else {
                                Log.d("TAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("TAG", "get failed with ", exception)
                        }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("Profile", "Error getting documents: ", exception)
            }
    }
}