package com.example.projekt_aplikacja_marketingowa

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
import java.text.SimpleDateFormat
import java.util.Locale

class PostDetailAdapter(val postDetails: MutableList<PostDetail>) : RecyclerView.Adapter<PostDetailAdapter.PostDetailViewHolder>() {
    inner class PostDetailViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_details_item, parent, false)
        return PostDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostDetailViewHolder, position: Int) {
        auth = Firebase.auth
        val db = Firebase.firestore
        val currentUser = auth.currentUser

        val post_details_user_profile_url: ImageView = holder.itemView.findViewById(R.id.post_details_user_profile_url_imageView)
        val post_details_user_name: TextView = holder.itemView.findViewById(R.id.post_details_user_name_textView)
        val post_details_date: TextView = holder.itemView.findViewById(R.id.post_details_date_textView)
        val post_details_title: TextView = holder.itemView.findViewById(R.id.post_details_title_textView)
        val post_details_price: TextView = holder.itemView.findViewById(R.id.post_details_price_textView)
        val post_details_description: TextView = holder.itemView.findViewById(R.id.post_details_description_textView)
        val post_details_image_url: ImageView = holder.itemView.findViewById(R.id.post_details_image_url_imageView)
        val followImageView: ImageView = holder.itemView.findViewById(R.id.follow_icon_imageView)

        post_details_title.text = postDetails[position].postDetailTitle
        post_details_price.text = postDetails[position].postDetailPrice
        post_details_description.text = postDetails[position].postDetailDescription

        val postDetailDate = postDetails[position]
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(postDetailDate.postDetailDate.toDate())
        post_details_date.text = formattedDate

        db.collection("users").document(postDetails[position].postDetailUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("UserProfileUrl").toString()).into(post_details_user_profile_url)
                }
            }

        db.collection("users").document(postDetails[position].postDetailUserId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("UserName")
                    if (userName != null) {
                        post_details_user_name.text = userName
                    }
                }
            }

        db.collection("posts").document(postDetails[position].postDetailPostId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.get().load(document.data?.get("PostImageUrl").toString()).into(post_details_image_url)
                }
            }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PostComments::class.java)
            intent.putExtra("PostId", postDetails[position].postDetailPostId)
            holder.itemView.context.startActivity(intent)
        }

        val docRef = db.collection("follows")
            .document(currentUser!!.uid + ":" + postDetails[position].postDetailPostId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    followImageView.setImageResource(R.drawable.baseline_favorite_24)
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }

        followImageView.setOnClickListener {
            db.collection("follows").document(currentUser!!.uid + ":" + postDetails[position].postDetailPostId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        db.collection("follows").document(currentUser!!.uid + ":" + postDetails[position].postDetailPostId)
                            .delete()
                            .addOnSuccessListener {
                                followImageView.setImageResource(R.drawable.baseline_favorite_border_24)
                            }
                            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }
                    } else {
                        val newFollow = hashMapOf(
                            "FollowUserId" to currentUser!!.uid,
                            "FollowPostId" to postDetails[position].postDetailPostId
                        )
                        db.collection("follows").document(currentUser!!.uid + ":" + postDetails[position].postDetailPostId)
                            .set(newFollow)
                            .addOnSuccessListener {
                                followImageView.setImageResource(R.drawable.baseline_favorite_24)
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
        return postDetails.size
    }
}