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
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter (val comments: MutableList<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val db = Firebase.firestore

        val comment_user_name : TextView = holder.itemView.findViewById(R.id.comments_user_name_textView)
        val comment_user_profile_url : ImageView = holder.itemView.findViewById(R.id.comments_user_profile_url_imageView)
        val comment_date : TextView = holder.itemView.findViewById(R.id.comments_comment_date_textView)
        val comment_text : TextView = holder.itemView.findViewById(R.id.comments_comment_text_textView)
        val commentDislikeImage : ImageView = holder.itemView.findViewById(R.id.thumb_down_icon_imageView)
        val commentLikeImage : ImageView = holder.itemView.findViewById(R.id.thumb_up_icon_imageView)
        val commentLikesCount : TextView = holder.itemView.findViewById(R.id.comments_likes_count_textView)

        comment_text.text = comments[position].commentText

        val commentDate = comments[position]
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(commentDate.commentDate.toDate())
        comment_date.text = formattedDate

        db.collection("users").document(comments[position].commentUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("UserProfileUrl").toString()).into(comment_user_profile_url)
                }
            }

        db.collection("users").document(comments[position].commentUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("UserName")
                    if (userName != null) {
                        comment_user_name.text = userName
                    }
                }
            }

        db.collection("likes")
            .document(currentUser!!.uid + ":" + comments[position].commentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if(document.data!!["Like"].toString().toInt() == 1) {
                        commentLikeImage.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
                    } else {
                        commentDislikeImage.setImageResource(R.drawable.baseline_thumb_down_off_alt_24)
                    }
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        db.collection("likes")
            .whereEqualTo("CommentId", comments[position].commentId)
            .get()
            .addOnSuccessListener { documents ->
                var likes = 0
                for (document in documents) {
                    likes += document.data["Like"].toString().toInt()
                }
                commentLikesCount.text = likes.toString()
            }

        commentLikeImage.setOnClickListener {
            val newLike = hashMapOf(
                "UserId" to currentUser!!.uid,
                "CommentId" to comments[position].commentId,
                "Like" to 1
            )
            db.collection("likes").document(currentUser!!.uid + ":" + comments[position].commentId)
                .set(newLike)
                .addOnSuccessListener {
                    commentLikeImage.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
                    commentDislikeImage.setImageResource(R.drawable.baseline_thumb_down_off_alt_24)
                    db.collection("likes")
                        .whereEqualTo("CommentId", comments[position].commentId)
                        .get()
                        .addOnSuccessListener { documents ->
                            var likes = 0
                            for (document in documents) {
                                likes += document.data["Like"].toString().toInt()
                            }
                            commentLikesCount.text = likes.toString()
                        }
                }
                .addOnFailureListener {
                    Log.d("TAG", "Error writing document")
                }
        }

        commentDislikeImage.setOnClickListener {
            val newLike = hashMapOf(
                "UserId" to currentUser!!.uid,
                "CommentId" to comments[position].commentId,
                "Like" to -1
            )
            db.collection("likes").document(currentUser!!.uid + ":" + comments[position].commentId)
                .set(newLike)
                .addOnSuccessListener {
                    commentLikeImage.setImageResource(R.drawable.baseline_thumb_up_off_alt_24)
                    commentDislikeImage.setImageResource(R.drawable.baseline_thumb_down_off_alt_24)
                    db.collection("likes")
                        .whereEqualTo("CommentId", comments[position].commentId)
                        .get()
                        .addOnSuccessListener { documents ->
                            var likes = 0
                            for (document in documents) {
                                likes += document.data["Like"].toString().toInt()
                            }
                            commentLikesCount.text = likes.toString()
                        }
                }
                .addOnFailureListener {
                    Log.d("TAG", "Error writing document")
                }
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}