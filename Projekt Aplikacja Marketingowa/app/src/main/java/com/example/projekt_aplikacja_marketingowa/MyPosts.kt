package com.example.projekt_aplikacja_marketingowa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyPosts : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        val toAddPostMyPosts : ImageView = findViewById(R.id.add_post_my_posts_imageView)
        val toHomeMyPosts : ImageView = findViewById(R.id.home_my_posts_imageView)
        val logoutMyPosts : ImageView = findViewById(R.id.logout_my_posts_imageView)
        val myPostsRecyclerView : RecyclerView = findViewById(R.id.my_posts_recyclerView)
        val toFavouritePostMyPosts : ImageView = findViewById(R.id.favourite_posts_my_posts_imageView)

        auth = Firebase.auth
        val db = Firebase.firestore
        val user = auth.currentUser

        val myPosts = mutableListOf<MyPost>()

        val myPostsAdapter = MyPostAdapter(myPosts)
        myPostsRecyclerView.adapter = myPostsAdapter
        myPostsRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("posts")
            .whereEqualTo("PostUserId", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("MyPosts", "${document.id} => ${document.data}")
                    myPosts.add(
                        MyPost(
                            document.id,
                            document.data["PostUserId"].toString(),
                            document.data["PostTitle"].toString(),
                            document.data["PostPrice"].toString(),
                            document.data["PostImageUrl"].toString()
                        )
                    )
                }
                myPostsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("MyPosts", "Error getting documents: ", exception)
            }

        toAddPostMyPosts.setOnClickListener {
            val intent = Intent(this, AddPost::class.java)
            startActivity(intent)
            finish()
        }

        toHomeMyPosts.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        toFavouritePostMyPosts.setOnClickListener {
            val intent = Intent(this, FavouritePosts::class.java)
            startActivity(intent)
            finish()
        }

        logoutMyPosts.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}