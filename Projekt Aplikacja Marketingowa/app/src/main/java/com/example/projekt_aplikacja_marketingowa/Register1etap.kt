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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage

class Register1etap : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                imageUri = data?.data
                imageView.setImageURI(imageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1etap)
        auth = Firebase.auth

        val nextButton: Button = findViewById(R.id.next_button)
        val toLogin1: TextView = findViewById(R.id.to_login_register1_textView)
        val userNameEditText: EditText = findViewById(R.id.user_name_editText)
        val userSurnameEditText: EditText = findViewById(R.id.user_surname_editText)
        val userDateEditText: EditText = findViewById(R.id.user_date_editText)
        val userImageImageView: ImageView = findViewById(R.id.user_image_imageView)
        val imgView : View = findViewById(R.id.img_view)
        val imgTextView : TextView = findViewById(R.id.img_textView)


        imageView = findViewById(R.id.imageView)

        userImageImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        toLogin1.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        nextButton.setOnClickListener {
            val user_name = userNameEditText.text.toString()
            val user_surname = userSurnameEditText.text.toString()
            val user_date = userDateEditText.text.toString()

            if (user_name.isNotEmpty() && user_surname.isNotEmpty() && user_date.isNotEmpty() && imageUri != null) {
                Toast.makeText(
                    baseContext,
                    "Poprawnie podano dane rejestracji",
                    Toast.LENGTH_SHORT
                ).show()

                if (imageUri != null) {
                    val storageReference = Firebase.storage.reference
                    val imageRef = storageReference.child("user_profile_images/$user_surname")
                    val uploadTask = imageRef.putFile(imageUri!!)

                    uploadTask.addOnSuccessListener { taskSnapshot ->
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            val intent = Intent(this, Register2etap::class.java).apply {
                                putExtra("user_name", user_name)
                                putExtra("user_surname", user_surname)
                                putExtra("user_date", user_date)
                                putExtra("downloadUrl", downloadUrl)
                            }
                            startActivity(intent)
                        }
                    }.addOnFailureListener { e ->
                        Log.w("MAIN", e.toString())
                        Toast.makeText(
                            baseContext,
                            "Nie udalo sie zaladowac zdjecia",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Wybierz zdjęcie profilowe",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    baseContext,
                    "Nie podano danych rejestracji",
                    Toast.LENGTH_SHORT
                ).show()
                if(user_name.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    userNameEditText.startAnimation(shakeAnimation)
                }
                if(user_surname.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    userSurnameEditText.startAnimation(shakeAnimation)
                }
                if(user_date.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    userDateEditText.startAnimation(shakeAnimation)
                }
                if(imageUri == null) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    imgView.startAnimation(shakeAnimation)
                    userImageImageView.startAnimation(shakeAnimation)
                    imgTextView.startAnimation(shakeAnimation)
                }
            }
        }
    }
}