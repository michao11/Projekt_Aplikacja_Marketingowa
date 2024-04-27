package com.example.projekt_aplikacja_marketingowa

import android.app.AlertDialog
import android.content.Intent
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

class MyPostAdapter (val myPosts: MutableList<MyPost>) : RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>() {
    inner class MyPostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_posts_list_item, parent, false)
        return MyPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        auth = Firebase.auth
        val db = Firebase.firestore

        val my_post_user_profile_url : ImageView = holder.itemView.findViewById(R.id.my_posts_user_profile_url_imageView)
        val my_post_user_name : TextView = holder.itemView.findViewById(R.id.my_posts_user_name_textView)
        val my_post_title : TextView = holder.itemView.findViewById(R.id.my_posts_title_textView)
        val my_post_price : TextView = holder.itemView.findViewById(R.id.my_posts_price_textView)
        val my_post_image_url : ImageView = holder.itemView.findViewById(R.id.my_posts_image_imageView)
        val deletePostImageView : ImageView = holder.itemView.findViewById(R.id.my_posts_delete_post_imageView)

        my_post_title.text = myPosts[position].myPostTitle
        my_post_price.text = myPosts[position].myPostPrice

        db.collection("users").document(myPosts[position].myPostUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("UserProfileUrl").toString()).into(my_post_user_profile_url)
                }
            }

        db.collection("users").document(myPosts[position].myPostUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("UserName")
                    if (userName != null) {
                        my_post_user_name.text = userName
                    }
                }
            }

        db.collection("posts").document(myPosts[position].myPostId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("PostImageUrl").toString()).into(my_post_image_url)
                }
            }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditPost::class.java)
            intent.putExtra("PostID", myPosts[position].myPostId)
            holder.itemView.context.startActivity(intent)
        }

        deletePostImageView.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Usun")
                .setMessage("Czy jestes pewny, ze chcesz usunac to ogloszenie?")
                .setPositiveButton("Anuluj") { dialog, which ->
                    dialog.dismiss()
                }.setNegativeButton("Usun") { dialog, which ->
                    db.collection("posts").document(myPosts[position].myPostId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("TAG", "DocumentSnapshot successfully deleted!")
                            myPosts.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, myPosts.size)
                        }
                        .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                }.show()
        }
    }

    override fun getItemCount(): Int {
        return myPosts.size
    }
}