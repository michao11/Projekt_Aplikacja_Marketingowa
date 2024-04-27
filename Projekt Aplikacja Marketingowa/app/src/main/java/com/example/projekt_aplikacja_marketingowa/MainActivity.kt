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

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton : Button = findViewById(R.id.login_button)
        val toRegister : TextView = findViewById(R.id.to_register_textView)
        val loginEmailEditText : EditText = findViewById(R.id.login_email_editText)
        val loginPasswordEditText : EditText = findViewById(R.id.login_password_editText)

        auth = Firebase.auth

        toRegister.setOnClickListener {
            val intent = Intent(this, Register1etap::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val login_user_email = loginEmailEditText.text.toString()
            val login_user_password = loginPasswordEditText.text.toString()

            if (login_user_email.isNotEmpty() && login_user_password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(login_user_email, login_user_password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "signInWithEmail:success")
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                            Toast.makeText(
                                baseContext,
                                "Zalogowano pomyslnie",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.w("TAG", "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Podano bledne dane logowania",
                                Toast.LENGTH_SHORT
                            ).show()
                            val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                            shakeAnimation.duration = 40
                            shakeAnimation.repeatCount = 5
                            shakeAnimation.repeatMode = Animation.REVERSE
                            loginEmailEditText.startAnimation(shakeAnimation)
                            loginPasswordEditText.startAnimation(shakeAnimation)
                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Nie podano danych logowania",
                    Toast.LENGTH_SHORT
                ).show()
                if(login_user_email.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    loginEmailEditText.startAnimation(shakeAnimation)
                }
                if(login_user_password.isEmpty()) {
                    val shakeAnimation = TranslateAnimation(0f, -20f, 0f, 0f)
                    shakeAnimation.duration = 40
                    shakeAnimation.repeatCount = 5
                    shakeAnimation.repeatMode = Animation.REVERSE
                    loginPasswordEditText.startAnimation(shakeAnimation)
                }
            }
        }
    }
}