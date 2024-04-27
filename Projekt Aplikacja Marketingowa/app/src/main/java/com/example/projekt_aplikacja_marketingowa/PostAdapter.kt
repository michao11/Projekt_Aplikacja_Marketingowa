package com.example.projekt_aplikacja_marketingowa

import android.content.Intent
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

class PostAdapter (val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        auth = Firebase.auth
        val db = Firebase.firestore

        val post_user_profile_url: ImageView = holder.itemView.findViewById(R.id.post_user_profile_url_imageView)
        val post_user_name : TextView = holder.itemView.findViewById(R.id.post_user_name_textView)
        val post_title : TextView = holder.itemView.findViewById(R.id.post_title_textView)
        val post_price : TextView = holder.itemView.findViewById(R.id.post_price_textView)
        val post_image_url: ImageView = holder.itemView.findViewById(R.id.post_image_imageView)

        post_title.text = posts[position].title
        post_price.text = posts[position].price

        db.collection("users").document(posts[position].userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("UserProfileUrl").toString()).into(post_user_profile_url)
                }
            }

        db.collection("users").document(posts[position].userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("UserName")
                    if (userName != null) {
                        post_user_name.text = userName
                    }
                }
            }

        db.collection("posts").document(posts[position].postId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("PostImageUrl").toString()).into(post_image_url)
                }
            }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PostDetails::class.java)
            intent.putExtra("postId", posts[position].postId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}