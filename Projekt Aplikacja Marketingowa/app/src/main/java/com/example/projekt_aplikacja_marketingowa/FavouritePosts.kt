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

class FavouritePosts : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_posts)

        val favouriteRecyclerView: RecyclerView = findViewById(R.id.favourite_posts_recyclerView)
        val favouritePostsMyPosts: ImageView = findViewById(R.id.my_posts_favourite_post_imageView)
        val favouritePostsHome: ImageView = findViewById(R.id.home_favourite_post_imageView)
        val favouritePostsFavouritePost: ImageView = findViewById(R.id.favourite_posts_favourite_post_imageView)

        auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        val follows = mutableListOf<String>()
        val favouritePosts = mutableListOf<FavouritePost>()

        val adapter = FavouritePostAdapter(favouritePosts)
        favouriteRecyclerView.adapter = adapter
        favouriteRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("follows")
            .whereEqualTo("FollowUserId", user!!.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    follows.add(document.data["FollowPostId"].toString())
                }
                for (postId in follows) {
                }
                for (follow in follows) {
                    val docRef = db.collection("posts").document(follow)
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                favouritePosts.add(
                                    FavouritePost(
                                        document.id,
                                        document.data!!["PostUserId"].toString(),
                                        document.data!!["PostTitle"].toString(),
                                        document.data!!["PostPrice"].toString(),
                                        document.data!!["PostImageUrl"].toString()
                                    )
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
                Log.w("FavouritePosts", "Error getting documents: ", exception)
            }

        favouritePostsFavouritePost.setOnClickListener {
            val intent = Intent(this, FavouritePosts::class.java)
            startActivity(intent)
            finish()
        }

        favouritePostsHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        favouritePostsMyPosts.setOnClickListener {
            val intent = Intent(this, MyPosts::class.java)
            startActivity(intent)
            finish()
        }
    }
}