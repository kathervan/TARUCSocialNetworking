package com.example.tarucsocialnetworking

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonRegister.setOnClickListener{
            performRegister()
        }

        textViewAlreadyHaveAnAccount.setOnClickListener{
            Log.d(TAG, "Try to show login activity")
            //launch the login activity
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
//1
        selectphoto_button_register.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null
//1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)
            selectphoto_button_register.alpha = 0f
        }
    }

    private fun performRegister() {

        val email = editTextRegisterEmail.text.toString()
        val password = editTextRegisterPassword.text.toString()
        val phoneNumber = editTextRegisterPhoneNumber.text.toString()
        val idName = editTextRegisterName.text.toString()

        if(selectedPhotoUri == null){
            Toast.makeText(this, "Please upload a photo", Toast.LENGTH_SHORT).show()
            return
        }

        if(password.isEmpty() && email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() && phoneNumber.isEmpty() && idName.isEmpty()){
            Toast.makeText(this, "Please fill in all the information correctly", Toast.LENGTH_LONG).show()
            return
        }
        if(idName.isEmpty()){
            Toast.makeText(this, "Please fill-up id name", Toast.LENGTH_SHORT).show()
            return
        }

        if(email.isEmpty()){
            Toast.makeText(this, "Please fill-up the email", Toast.LENGTH_SHORT).show()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please fill-up correct email format", Toast.LENGTH_SHORT).show()
            return
        }

        if(phoneNumber.isEmpty()){
            Toast.makeText(this, "Please fill-up phone no", Toast.LENGTH_SHORT).show()
            return
        }
        if(password.isEmpty()) {
            Toast.makeText(this, "Please fill-up password", Toast.LENGTH_SHORT).show()
            return
        }
        if(password.length < 5){
            Toast.makeText(this, "The password must be more than 4 characters", Toast.LENGTH_SHORT).show()
            return
        }
        /*val user = auth.currentUser
                user?.sendEmailVerification()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        }
                    }*/

        //Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")
        //Log.d(TAG, "Failed to create user: ${it.message}")
//3
            //Firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    // else if successful
                    Toast.makeText(this, "Successfully create user account", Toast.LENGTH_SHORT).show()
                    uploadImageToFirebaseStorage()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Email has been used.", Toast.LENGTH_SHORT).show()
                }
    }
//2
    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG,"File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
            }
    }
//5
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, editTextRegisterName.text.toString(),profileImageUrl,
            editTextRegisterPhoneNumber.text.toString())

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this, InterestFieldActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }
}
//4
@Parcelize
class User(val uid: String, val username: String, val profileImage: String, val userPhoneNo: String): Parcelable{
    constructor() : this("", "","", "")
}