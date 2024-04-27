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

class PostDetails : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        val myPostsPostDetailsImageView : ImageView = findViewById(R.id.my_posts_post_details_imageView)
        val toHomePostDetailsImageView : ImageView = findViewById(R.id.to_home_post_details_imageView)
        val toFavouritePostsPostDetails : ImageView = findViewById(R.id.favourite_posts_post_details_imageView)
        val postDetailsRecyclerView : RecyclerView = findViewById(R.id.post_details_recyclerView)

        auth = Firebase.auth
        val db = Firebase.firestore

        val postId = intent.getStringExtra("postId")
        val postDetails = mutableListOf<PostDetail>()

        val postDetailsAdapter = PostDetailAdapter(postDetails)
        postDetailsRecyclerView.adapter = postDetailsAdapter
        postDetailsRecyclerView.layoutManager = LinearLayoutManager(this)


        db.collection("posts").whereEqualTo("PostId", postId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("PostDetails", "${document.id} => ${document.data}")
                    postDetails.add(
                        PostDetail(
                            document.id,
                            document.data["PostUserId"].toString(),
                            document.data["PostDate"] as com.google.firebase.Timestamp,
                            document.data["PostTitle"].toString(),
                            document.data["PostPrice"].toString(),
                            document.data["PostDescription"].toString(),
                            document.data["PostImageUrl"].toString(),
                        )
                    )
                }
                postDetailsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("Home", "Error getting documents: ", exception)
            }

        myPostsPostDetailsImageView.setOnClickListener {
            val intent = Intent(this, MyPosts::class.java)
            startActivity(intent)
            finish()
        }

        toHomePostDetailsImageView.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        toFavouritePostsPostDetails.setOnClickListener {
            val intent = Intent(this, FavouritePosts::class.java)
            startActivity(intent)
            finish()
        }
    }
}