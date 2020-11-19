package com.example.tarucsocialnetworking

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tarucsocialnetworking.UserListActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.user_row.view.*

class UserListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val subject = intent.getStringExtra("Subject")

        supportActionBar?.title = subject

        recyclerview_friendlist.adapter
        recyclerview_friendlist.layoutManager = LinearLayoutManager(this)
        fetchUsers()
    }

    companion object {
        val USER_KEY = "USER_KEY"
        var invite = false
    }

    private fun test(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Forgot Password")
        val view = layoutInflater.inflate(R.layout.activity_forgot_password, null)
        builder.setView(view)
        builder.setPositiveButton("Accept", DialogInterface.OnClickListener { _, _ ->
            invite = true;
        })
        builder.setNegativeButton("Decline", DialogInterface.OnClickListener { _, _ ->
            invite = false;
        })
        builder.show()
    }


    private fun fetchUsers(){
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val subject = intent.getStringExtra("Subject")

        FirebaseDatabase.getInstance().getReference("/users-interest/$subject")
        //FirebaseDatabase.getInstance().getReference("/users")
            .ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()
                    snapshot.children.forEach{
                        val user = it.getValue(User::class.java)
                        //val user = it.getValue(User::class.java)
                        if (user!!.uid != (firebase.uid)) {
                        //if(user != null){
                            adapter.add(UserItem(user))
                        }
                    }
                    adapter.setOnItemClickListener{item, view->
                        val userItem = item as UserItem
                        /*val toUser = userItem.toString()
                        val ref = FirebaseDatabase.getInstance().getReference("/user-invite/$firebase.uid/${userItem.user}")

                        val inviteUser = InviteUser(firebase.uid, invite, toUser)
                            ref.setValue(inviteUser)*/

                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(USER_KEY, userItem.user)
                        startActivity(intent)
                        finish()
                    }
                    recyclerview_friendlist.adapter = adapter
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    class UserItem(val user: User): Item<ViewHolder>(){
    //class UserItem(val user: User): Item<ViewHolder>(){
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.username_textview_friend_list.text = user.username

            Picasso.get().load(user.profileImage).into(viewHolder.itemView.imageView_friend_list)
        }

        override fun getLayout(): Int {
            return R.layout.user_row
        }
    }
}

