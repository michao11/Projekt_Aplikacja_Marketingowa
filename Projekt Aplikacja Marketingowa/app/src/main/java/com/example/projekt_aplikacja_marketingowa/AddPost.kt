package com.example.projekt_aplikacja_marketingowa

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddPost : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var imageView1: ImageView
    private var imageUri1: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data1: Intent? = it.data
                imageUri1 = data1?.data
                imageView1.setImageURI(imageUri1)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        val addPostTitleEditText : EditText = findViewById(R.id.add_post_title_editText)
        val addPostPriceEditText : EditText = findViewById(R.id.add_post_price_editText)
        val addPostDescriptionEditText : EditText = findViewById(R.id.add_post_description_editText)
        val addProductImageImageView : ImageView = findViewById(R.id.add_product_image_imageView)
        val addPostButton : Button = findViewById(R.id.add_post_button)
        val backArrowImageView : ImageView = findViewById(R.id.back_arrow_imageView)
        val addPostView : View = findViewById(R.id.view)
        val addPostProductTextView : TextView = findViewById(R.id.product_add_textView)

        imageView1 = findViewById(R.id.imageView1)
        val db = Firebase.firestore
        auth = Firebase.auth
        val user = auth.currentUser

        addProductImageImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        addPostButton.setOnClickListener {
            val add_post_title = addPostTitleEditText.text.toString()
            val add_post_price = addPostPriceEditText.text.toString()
            val add_post_description = addPostDescriptionEditText.text.toString()

            if (add_post_title.isNotEmpty() && add_post_price.isNotEmpty() && add_post_description.isNotEmpty() && imageUri1 != null) {
                Toast.makeText(
                    baseContext,
                    "Pomyslnie dodano ogloszenie",
                    Toast.LENGTH_SHORT
                ).show()

                if (imageUri1 != null) {
                    val storageReference = Firebase.storage.reference
                    val userId = user?.uid
                    if (userId != null) {
                        val userDocRef = db.collection("users").document(userId)
                        userDocRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val imageRef = storageReference.child("post_images/$userId")
                                    val uploadTask = imageRef.putFile(imageUri1!!)

                                    uploadTask.addOnSuccessListener { taskSnapshot ->
                                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                                            val downloadUrl1 = uri.toString()
                                            val newPost = hashMapOf(
                                                "PostId" to "",
                                                "PostUserId" to userId,
                                                "PostDate" to FieldValue.serverTimestamp(),
                                                "PostTitle" to add_post_title,
                                                "PostPrice" to add_post_price + " zł",
                                                "PostDescription" to add_post_description,
                                                "PostImageUrl" to downloadUrl1
                                            )
                                            db.collection("posts")
                                                .add(newPost)
                                                .addOnSuccessListener { documentReference ->
                                                    db.collection("posts")
                                                        .document(documentReference.id)
                                                        .update("PostId", documentReference.id)
                                                        .addOnSuccessListener {
                                                            Log.d(
                                                                "AddPost", "DocumentSnapshot successfully written!"
                                                            )
                                                            val intent = Intent(this, MyPosts::class.java)
                                                            startActivity(intent)
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.w("TAG", "Error adding document", e)
                                                            Toast.makeText(
                                                                baseContext,
                                                                "Podano niepoprawne dane",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                        }
                                    }
                                } else {
                                    Log.d("TAG", "No such document")
                                }
                            }
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Wybierz zdjęcie produktu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Nie podano danych",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    baseContext,
                    "Nie podano danych ogloszenia",
                    Toast.LENGTH_SHORT
                ).show()
                if(add_post_title.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    addPostTitleEditText.startAnimation(shakeAnimation)
                }
                if(add_post_price.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    addPostPriceEditText.startAnimation(shakeAnimation)
                }
                if(add_post_description.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    addPostDescriptionEditText.startAnimation(shakeAnimation)
                }
                if(imageUri1 == null) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    addPostView.startAnimation(shakeAnimation)
                    addProductImageImageView.startAnimation(shakeAnimation)
                    addPostProductTextView.startAnimation(shakeAnimation)
                }
            }
        }

        backArrowImageView.setOnClickListener {
            val intent = Intent(this, MyPosts::class.java)
            startActivity(intent)
            finish()
        }
    }
}