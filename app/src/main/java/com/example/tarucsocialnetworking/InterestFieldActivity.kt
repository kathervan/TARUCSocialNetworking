package com.example.tarucsocialnetworking

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_interest_field.*
import java.util.*

class InterestFieldActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
    }

    var toUser: User? = null

    var subject = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_field)
        //setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true);

        val username = FirebaseDatabase.getInstance().getReference("users/uid")
        actionBar?.title = "${toUser?.username}"

        fetchCurrentUser()

        verifyUserIsLoggedIn()

        buttonInterestField()
    }

    private fun buttonInterestField(){

        btnArt.setOnClickListener{

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val uri = currentUser!!.profileImage
            subject = "Art"
            val username = currentUser!!.username

            val ref = FirebaseDatabase.getInstance().getReference("users-interest/$subject").child(uid)
            val interestUser = InterestUser(uid, username,uri)

            ref.setValue(interestUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Fail ", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("Subject", subject)
            startActivity(intent)
        }

        btnAnimal.setOnClickListener{

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val uri = currentUser!!.profileImage
            subject = "Animal"
            val username = currentUser!!.username

            val ref = FirebaseDatabase.getInstance().getReference("users-interest/$subject").child(uid)
            val interestUser = InterestUser(uid, username,uri)

            ref.setValue(interestUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Fail ", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("Subject", subject)
            startActivity(intent)
        }

        btnGaming.setOnClickListener{

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val uri = currentUser!!.profileImage
            subject = "Gaming"
            val username = currentUser!!.username

            val ref = FirebaseDatabase.getInstance().getReference("users-interest/$subject").child(uid)
            val interestUser = InterestUser(uid, username,uri)

            ref.setValue(interestUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Fail ", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("Subject", subject)
            startActivity(intent)
        }

        btnHistory.setOnClickListener{

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val uri = currentUser!!.profileImage
            subject = "History"
            val username = currentUser!!.username

            val ref = FirebaseDatabase.getInstance().getReference("users-interest/$subject").child(uid)
            val interestUser = InterestUser(uid, username,uri)

            ref.setValue(interestUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Fail ", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("Subject", subject)
            startActivity(intent)
        }

        btnMusic.setOnClickListener{

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val uri = currentUser!!.profileImage
            subject = "Music"
            val username = currentUser!!.username

            val ref = FirebaseDatabase.getInstance().getReference("users-interest/$subject").child(uid)
            val interestUser = InterestUser(uid, username,uri)

            ref.setValue(interestUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Fail ", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("Subject", subject)
            startActivity(intent)
        }

        btnNature.setOnClickListener{

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val uri = currentUser!!.profileImage
            val username = currentUser!!.username
            subject = "Nature"

            val ref = FirebaseDatabase.getInstance().getReference("users-interest/$subject").child(uid)
            val interestUser = InterestUser(uid, username, uri)

            ref.setValue(interestUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Fail ", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("Subject", subject)
            startActivity(intent)
        }

        btnTravel.setOnClickListener{

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val uri = currentUser!!.profileImage
            subject = "Travel"
            val username = currentUser!!.username

            val ref = FirebaseDatabase.getInstance().getReference("users-interest/$subject").child(uid)
            val interestUser = InterestUser(uid, username,uri)

            ref.setValue(interestUser)
                .addOnSuccessListener {
                    Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Fail ", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, UserListActivity::class.java)
            intent.putExtra("Subject", subject)
            startActivity(intent)
        }
    }


    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("InterestField", "Current User ${currentUser?.username}")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_Friend_List -> {
                val intent = Intent(this, FriendListActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

@Parcelize
class InterestUser(val uid:String, val username : String ,val profileImage: String): Parcelable {
    constructor() : this("","","")
}