package com.example.projekt_aplikacja_marketingowa

import android.app.Activity
import android.content.Intent
import android.media.Image
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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditPost : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var imageView2: ImageView
    private var imageUri2: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data1: Intent? = it.data
                imageUri2 = data1?.data
                imageView2.setImageURI(imageUri2)
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        imageView2 = findViewById(R.id.imageView2)
        auth = Firebase.auth
        val db = Firebase.firestore
        val user = auth.currentUser
        val PostID = intent.getStringExtra("PostID")

        val editPostTitleEditText: EditText = findViewById(R.id.edit_post_title_editText)
        val editPostPriceEditText: EditText = findViewById(R.id.edit_post_price_editText)
        val editPostDescriptionEditText: EditText = findViewById(R.id.edit_post_description_editText)
        val editPostProductImageView: ImageView = findViewById(R.id.edit_post_product_imageView)
        val editPostSaveChangesButton: Button = findViewById(R.id.edit_post_save_changes_button)
        val editPostBackArrowImage: ImageView = findViewById(R.id.back_arrow_edit_post_imageView)
        val editPostView : View = findViewById(R.id.view2)
        val editPostAddImgTextView : TextView = findViewById(R.id.edit_post_add_img_textView)

        editPostProductImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        editPostSaveChangesButton.setOnClickListener {
            val edit_post_title = editPostTitleEditText.text.toString()
            val edit_post_price = editPostPriceEditText.text.toString()
            val edit_post_description = editPostDescriptionEditText.text.toString()

            if (edit_post_title.isNotEmpty() && edit_post_price.isNotEmpty() && edit_post_description.isNotEmpty() && imageUri2 != null) {
                Toast.makeText(
                    baseContext,
                    "Pomyslnie edytowano ogloszenie",
                    Toast.LENGTH_SHORT
                ).show()

                val storageReference = Firebase.storage.reference
                val userId = user?.uid
                if (userId != null) {
                    val userDocRef = db.collection("users").document(userId)
                    userDocRef.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val imageRef = storageReference.child("post_edit_images/$userId")
                                val uploadTask = imageRef.putFile(imageUri2!!)

                                uploadTask.addOnSuccessListener { taskSnapshot ->
                                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                                        val downloadUrl1 = uri.toString()
                                        val editPost = mutableMapOf<String, Any>(
                                            "PostTitle" to edit_post_title,
                                            "PostPrice" to edit_post_price + " zł",
                                            "PostDescription" to edit_post_description
                                        )
                                        if (PostID != null) {
                                            db.collection("posts").document(PostID)
                                                .update(editPost)
                                                .addOnSuccessListener {
                                                    db.collection("posts").document(PostID)
                                                        .update("PostImageUrl", downloadUrl1)
                                                        .addOnSuccessListener {
                                                            Log.d(
                                                                "EditPost", "Document successfully updated!"
                                                            )
                                                            val intent = Intent(this, MyPosts::class.java)
                                                            startActivity(intent)
                                                        }
                                                        .addOnFailureListener {
                                                            Log.w("EditPost", "Error updating image URL")
                                                            Toast.makeText(
                                                                baseContext,
                                                                "Nie udalo się edytowac obrazu",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                                .addOnFailureListener {
                                                    Log.w("EditPost", "Error updating document")
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
                    "Nie podano danych ogłoszenia",
                    Toast.LENGTH_SHORT
                ).show()
                if(edit_post_title.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    editPostTitleEditText.startAnimation(shakeAnimation)
                }
                if(edit_post_price.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    editPostPriceEditText.startAnimation(shakeAnimation)
                }
                if(edit_post_description.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    editPostDescriptionEditText.startAnimation(shakeAnimation)
                }
                if(imageUri2 == null) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    editPostView.startAnimation(shakeAnimation)
                    editPostProductImageView.startAnimation(shakeAnimation)
                    editPostAddImgTextView.startAnimation(shakeAnimation)
                }
            }
        }

        editPostBackArrowImage.setOnClickListener {
            val intent = Intent(this, MyPosts::class.java)
            startActivity(intent)
            finish()
        }
    }
}