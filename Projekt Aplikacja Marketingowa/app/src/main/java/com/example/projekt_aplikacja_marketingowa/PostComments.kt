package com.example.projekt_aplikacja_marketingowa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostComments : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_comments)

        auth = Firebase.auth
        val PostId = intent.getStringExtra("PostId")
        val db = Firebase.firestore

        val commentsBackArrow : ImageView = findViewById(R.id.comments_back_arrow_imageView)
        val commentRecyclerView : RecyclerView = findViewById(R.id.comments_recyclerView)
        val commentText : EditText = findViewById(R.id.comments_comment_editText)
        val commentSendButton : Button = findViewById(R.id.comments_send_comment_button)

        val comments = mutableListOf<Comment>()

        val adapter = CommentAdapter(comments)
        commentRecyclerView.adapter = adapter
        commentRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("comments").whereEqualTo("CommentPostId", PostId)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Comments", "${document.id} => ${document.data}")
                    comments.add(Comment(document.id,
                        document.data["CommentUserId"].toString(),
                        document.data["CommentPostId"].toString(),
                        document.data["CommentText"].toString(),
                        document.data["CommentDate"] as com.google.firebase.Timestamp
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d("Comments", "Error getting documents: ", exception)
            }

        commentSendButton.setOnClickListener {
            val comment_text = commentText.text.toString()

            if (comment_text.isNotEmpty()) {
                val userDocRef = db.collection("users").document(auth.currentUser!!.uid)
                userDocRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val newComment = hashMapOf(
                                "CommentUserId" to auth.currentUser!!.uid,
                                "CommentPostId" to PostId,
                                "CommentText" to commentText.text.toString(),
                                "CommentDate" to FieldValue.serverTimestamp()
                            )

                            db.collection("comments")
                                .add(newComment)
                                .addOnSuccessListener {
                                    Log.d("Comments", "DocumentSnapshot successfully written!")
                                    commentText.text.clear()
                                    commentText.onEditorAction(0)
                                    db.collection("comments").whereEqualTo("CommentPostId", PostId)
                                        .get()
                                        .addOnSuccessListener { result ->
                                            comments.clear()
                                            for (document in result) {
                                                Log.d(
                                                    "Comments",
                                                    "${document.id} => ${document.data}"
                                                )
                                                comments.add(
                                                    Comment(
                                                        document.id,
                                                        document.data["CommentUserId"].toString(),
                                                        document.data["CommentPostId"].toString(),
                                                        document.data["CommentText"].toString(),
                                                        document.data["CommentDate"] as com.google.firebase.Timestamp
                                                    )
                                                )
                                            }
                                            adapter.notifyDataSetChanged()
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.d(
                                                "Comments",
                                                "Error getting documents: ",
                                                exception
                                            )
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Comment", "Error adding document", e)
                                }
                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Nie wpisano tresci komentarza",
                    Toast.LENGTH_SHORT
                ).show()
                if(comment_text.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    commentText.startAnimation(shakeAnimation)
                }
            }
        }

        commentsBackArrow.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}