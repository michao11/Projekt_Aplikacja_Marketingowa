package com.example.projekt_aplikacja_marketingowa

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val postHomeRecyclerView : RecyclerView = findViewById(R.id.post_home_recyclerView)
        val toMyPostsHome : ImageView = findViewById(R.id.my_posts_home_imageView)
        val toFavouritePostHome : ImageView = findViewById(R.id.favourite_posts_home_imageView)

        auth = Firebase.auth

        val posts = mutableListOf<Post>()

        val adapter = PostAdapter(posts)
        postHomeRecyclerView.adapter = adapter
        postHomeRecyclerView.layoutManager = LinearLayoutManager(this)

        val db = Firebase.firestore

        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                posts.clear()
                for (document in result) {
                    Log.d("Home", "${document.id} => ${document.data}")
                    posts.add(
                        Post(
                            document.id,
                            document.data["PostUserId"].toString(),
                            document.data["PostTitle"].toString(),
                            document.data["PostPrice"].toString(),
                            document.data["PostImageUrl"].toString()
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("Home", "Error getting documents: ", exception)
            }

        toMyPostsHome.setOnClickListener {
            val intent = Intent(this, MyPosts::class.java)
            startActivity(intent)
            finish()
        }

        toFavouritePostHome.setOnClickListener {
            val intent = Intent(this, FavouritePosts::class.java)
            startActivity(intent)
            finish()
        }
    }
}