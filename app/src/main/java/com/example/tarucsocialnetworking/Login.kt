package com.example.tarucsocialnetworking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin.setOnClickListener{
            val email = editTextEmailAddress.text.toString()
            val password = editTextPassword.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)

        }
        textViewCreateAccount.setOnClickListener{
            finish()
        }
    }
}