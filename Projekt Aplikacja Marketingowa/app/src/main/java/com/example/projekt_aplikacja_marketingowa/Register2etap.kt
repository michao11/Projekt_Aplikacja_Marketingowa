package com.example.projekt_aplikacja_marketingowa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class Register2etap : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register2etap)

        val registerButton : Button = findViewById(R.id.register_button)
        val toLogin2 : TextView = findViewById(R.id.to_login_textView)
        val registerEmailEditText : EditText = findViewById(R.id.register2_email_editText)
        val registerPasswordEditText : EditText = findViewById(R.id.register2_password_editText)
        val registerRepeatPasswordEditText : EditText = findViewById(R.id.register2_repeatpassword_editText)

        val user_name = intent.getStringExtra("user_name")
        val user_surname = intent.getStringExtra("user_surname")
        val user_date = intent.getStringExtra("user_date")
        val downloadUrl = intent.getStringExtra("downloadUrl")

        auth = Firebase.auth
        val db = Firebase.firestore

        toLogin2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener {
            val user_email = registerEmailEditText.text.toString()
            val user_password = registerPasswordEditText.text.toString()
            val user_repeat_password = registerRepeatPasswordEditText.text.toString()

            if (user_email.isNotEmpty() && user_password.isNotEmpty() && user_repeat_password.isNotEmpty()) {
                if (user_password == user_repeat_password) {
                    auth.createUserWithEmailAndPassword(user_email, user_password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null) {
                                    val newUser = hashMapOf(
                                        "UserId" to user.uid,
                                        "UserName" to user_name,
                                        "UserSurname" to user_surname,
                                        "UserDate" to user_date,
                                        "UserProfileUrl" to downloadUrl,
                                        "UserEmail" to user_email
                                    )
                                    db.collection("users").document(user.uid)
                                        .set(newUser)
                                        .addOnSuccessListener {
                                            Log.d("TAG", "DocumentSnapshot successfully written!")
                                            val intent = Intent(this, HomeActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                            Toast.makeText(
                                                baseContext,
                                                "Zarejestrowano pomyslnie",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "Error writing document", e)
                                            Toast.makeText(
                                                baseContext,
                                                "Podano niepoprawne dane rejestracji",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Rejestracja nie powiodla się",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.w("TAG", "createUserWithEmailAndPassword:failure", task.exception)
                            }
                        }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Podane hasla nie sa takie same",
                        Toast.LENGTH_SHORT
                    ).show()
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    registerPasswordEditText.startAnimation(shakeAnimation)
                    registerRepeatPasswordEditText.startAnimation(shakeAnimation)
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Nie podano danych rejestracji",
                    Toast.LENGTH_SHORT
                ).show()
                if(user_email.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    registerEmailEditText.startAnimation(shakeAnimation)
                }
                if(user_password.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    registerPasswordEditText.startAnimation(shakeAnimation)
                }
                if(user_repeat_password.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    registerRepeatPasswordEditText.startAnimation(shakeAnimation)
                }
            }
        }
    }
}