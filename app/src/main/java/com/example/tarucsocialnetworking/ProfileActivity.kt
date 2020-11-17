package com.example.tarucsocialnetworking

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.selectphoto_imageview_profile
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    var toUser: User? = null

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        //supportActionBar?.title = ""

        auth = FirebaseAuth.getInstance()

        selectphoto_button_profile.setOnClickListener{
            Log.d(RegisterActivity.TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0)

        }

        btn_change_password.setOnClickListener{
            changePassword()
        }

        btn_delete_account.setOnClickListener{
            deleteAccount()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            //Log.d(RegisterActivity.TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_profile.setImageBitmap(bitmap)

            selectphoto_button_profile.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                //Log.d(RegisterActivity.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    //Log.d(RegisterActivity.TAG,"File Location: $it")

                    updateProfile(it.toString())
                }
            }
            .addOnFailureListener{
                // do some logging here
            }
    }

    private fun updateProfile(profileImageUrl: String){
        val name = editText_profile_user_name.text.toString()
        val phoneNo = editText_profile_phone_no.text.toString()
        val uid = FirebaseAuth.getInstance().uid ?: ""

        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid");

        val profileUpdate = ProfileUpdate(uid, name, profileImageUrl, phoneNo)
        reference.setValue(profileUpdate)

    }

    private fun deleteAccount(){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid");
        val builder = AlertDialog.Builder(this)
        builder.setTitle("")
        val view = layoutInflater.inflate(R.layout.delete_account, null)

        val user = auth.currentUser!!

        builder.setView(view)
        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            //Toast.makeText(this, "Successfully delete this account", Toast.LENGTH_SHORT).show()
            user.delete()
                .addOnCompleteListener {
                    Toast.makeText(this, "Successfully delete this account", Toast.LENGTH_SHORT).show()
                }
            reference.removeValue()
            startActivity(Intent(this, RegisterActivity::class.java))
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->  })
        builder.show()
    }

    private fun changePassword(){
        val name = editText_profile_user_name.text.toString()
        val phoneNo = editText_profile_phone_no.text.toString()

        if(selectedPhotoUri == null){
            Toast.makeText(this, "Please upload a photo", Toast.LENGTH_SHORT).show()
            return
        }

        if(name.isEmpty()){
            Toast.makeText(this, "Please fill-up the name", Toast.LENGTH_SHORT).show()
            return
        }

        if(phoneNo.isEmpty()){
            Toast.makeText(this, "Please fill-up phone no", Toast.LENGTH_SHORT).show()
            return
        }

        if( editText_profile_current_password.text.isNotEmpty() &&
            editText_profile_new_password.text.isNotEmpty() &&
            editText_profile_reenter_password.text.isNotEmpty() &&
            editText_profile_phone_no.text.isNotEmpty()){

            if(editText_profile_new_password.text.toString().equals(editText_profile_reenter_password.text.toString())){

                val user = auth.currentUser
                if(user != null && user.email != null){
                    val credential = EmailAuthProvider.getCredential(user.email!!, editText_profile_current_password.text.toString())

                    user.reauthenticate(credential)
                        .addOnCompleteListener {
                            if(it.isSuccessful){
                                //Toast.makeText(this, "Re-Authentication failed", Toast.LENGTH_SHORT).show()

                                user!!.updatePassword(editText_profile_new_password.text.toString())
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            uploadImageToFirebaseStorage()
                                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }else{
                                Toast.makeText(this, "Invalid current password", Toast.LENGTH_LONG).show()
                            }
                        }
                }else{
                    startActivity(Intent(this, Login::class.java))
                }
            }else{
                Toast.makeText(this, "Password mismatching", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Please enter all the fields.", Toast.LENGTH_SHORT).show()
        }
    }


}

@Parcelize
class ProfileUpdate(val uid: String, val username: String, val profileImage: String, val userPhoneNo: String): Parcelable{
    constructor() : this("", "","", "")
}