package com.example.tarucsocialnetworking

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    //lateinit var mForgetPass : TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        //mForgetPass = findViewById(R.id.textViewForgotPassword)

        textViewForgotPassword.setOnClickListener{
            //val forgetIntent = Intent(applicationContext, ForgotPasswordActivity::class.java)
            //startActivity(forgetIntent)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Forgot Password")
            val view = layoutInflater.inflate(R.layout.activity_forgot_password, null)
            val username = view.findViewById<EditText>(R.id.editTextEnter_email_forgotten_password)
            builder.setView(view)
            builder.setPositiveButton("Reset", DialogInterface.OnClickListener { _, _ ->
                forgotPassword(username)
            })
            builder.setNegativeButton("Close", DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        }

        buttonLogin.setOnClickListener{
            performLogin()
        }
        textViewCreateAccount.setOnClickListener{
            finish()
        }
    }

    private fun forgotPassword(username : EditText){
        if(username.text.toString().isEmpty()){
            Toast.makeText(this, "Please fill-up the email", Toast.LENGTH_SHORT).show()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username.text.toString()).matches()){
            Toast.makeText(this, "Please fill-up correct email format", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(username.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email sent. Please check your inbox.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun performLogin(){
        val email = editTextLoginEmailAddress.text.toString()
        val password = editTextLoginPassword.text.toString()



        if (email.isEmpty()) {
            Toast.makeText(this, "Please fill up the email", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please fill up the password", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Please fill up the email and password.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editTextLoginEmailAddress.error = "Please enter valid email"
                    editTextLoginEmailAddress.requestFocus()
                }
                if (password.isEmpty()){
                    editTextLoginPassword.error = "Please enter valid password"
                    editTextLoginPassword.requestFocus()
                }

                val intent = Intent(this, InterestFieldActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

