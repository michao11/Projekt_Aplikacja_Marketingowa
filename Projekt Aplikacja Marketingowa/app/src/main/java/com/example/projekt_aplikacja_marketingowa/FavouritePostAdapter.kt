package com.example.projekt_aplikacja_marketingowa

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class FavouritePostAdapter (val favouritePosts: MutableList<FavouritePost>) : RecyclerView.Adapter<FavouritePostAdapter.FavouritePostViewHolder>() {
    inner class FavouritePostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    private lateinit var auth: FirebaseAuth
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritePostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favourite_posts_list_item, parent, false)
        return FavouritePostViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouritePostViewHolder, position: Int) {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = Firebase.firestore

        val favourite_post_user_profile_url : ImageView = holder.itemView.findViewById(R.id.favourite_posts_user_profile_url_imageView)
        val favourite_post_user_name : TextView = holder.itemView.findViewById(R.id.favourite_posts_user_name_textView)
        val favourite_post_title : TextView = holder.itemView.findViewById(R.id.favourite_posts_post_title_textView)
        val favourite_post_price : TextView = holder.itemView.findViewById(R.id.favourite_posts_post_price_textView)
        val favourite_post_image_url : ImageView = holder.itemView.findViewById(R.id.favourite_posts_image_url_imageView)
        val favouritePostsImage : ImageView = holder.itemView.findViewById(R.id.favourite_posts_favourite_icon_imageView)

        favourite_post_title.text = favouritePosts[position].favouritePostTitle
        favourite_post_price.text = favouritePosts[position].favouritePostPrice

        db.collection("users").document(favouritePosts[position].favouritePostUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("UserProfileUrl").toString()).into(favourite_post_user_profile_url)
                }
            }

        db.collection("users").document(favouritePosts[position].favouritePostUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("UserName")
                    if (userName != null) {
                        favourite_post_user_name.text = userName
                    }
                }
            }

        db.collection("posts").document(favouritePosts[position].favouritePostId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("PostImageUrl").toString()).into(favourite_post_image_url)
                }
            }

        val docRef = db.collection("follows")
            .document(currentUser!!.uid + ":" + favouritePosts[position].favouritePostId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    favouritePostsImage.setImageResource(R.drawable.baseline_favorite_24)
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        favouritePostsImage.setOnClickListener {
            db.collection("follows").document(currentUser!!.uid + ":" + favouritePosts[position].favouritePostId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        db.collection("follows").document(currentUser!!.uid + ":" + favouritePosts[position].favouritePostId)
                            .delete()
                            .addOnSuccessListener {
                                favouritePostsImage.setImageResource(R.drawable.baseline_favorite_border_24)
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                    } else {
                        val newFollow = hashMapOf(
                            "FollowUserId" to currentUser!!.uid,
                            "FollowPostId" to favouritePosts[position].favouritePostId
                        )
                        db.collection("follows").document(currentUser!!.uid + ":" + favouritePosts[position].favouritePostId)
                            .set(newFollow)
                            .addOnSuccessListener {
                                favouritePostsImage.setImageResource(R.drawable.baseline_favorite_24)
                            }
                            .addOnFailureListener { e -> Log.w("Follow", "Error writing document", e) }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "get failed with ", exception)
                }
        }
    }

    override fun getItemCount(): Int {
        return favouritePosts.size
    }
}