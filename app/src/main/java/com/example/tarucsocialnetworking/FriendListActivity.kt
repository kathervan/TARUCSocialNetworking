package com.example.tarucsocialnetworking

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.friend_list_row.*
import kotlinx.android.synthetic.main.user_row.view.*

class FriendListActivity : AppCompatActivity() {

    //lateinit var option : Spinner
    //lateinit var result : TextView
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        recyclerview_friendlist.adapter
        recyclerview_friendlist.layoutManager = LinearLayoutManager(this)

        fetchUsers()


    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    var toUser: User? = null

    private fun fetchUsers(){
        // get current user
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        //FirebaseDatabase.getInstance().getReference("/users")
        FirebaseDatabase.getInstance().getReference("/users-friend")
            .ref.addListenerForSingleValueEvent(object: ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()

                    snapshot.children.forEach{
                        Log.d("FriendList", it.toString())
                        //val user = it.getValue(FriendList::class.java)
                        val user = it.getValue(User::class.java)

                        if (user != null) {
                            adapter.add(FriendItem(user))
                        }
                    }

                    adapter.setOnItemClickListener{item, view->
                        val friendItem = item as FriendItem



                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        //intent.putExtra(USER_KEY, userItem.user.username)
                        intent.putExtra(USER_KEY, friendItem.user)
                        startActivity(intent)
                        finish()

                    }
                    recyclerview_friendlist.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }

    private fun deleteFriend(){

        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
        /*
        val toId = toUser?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/users-friend/$toId");
        val builder = AlertDialog.Builder(this)
        builder.setTitle("")
        val view = layoutInflater.inflate(R.layout.delete_account, null)

        val toUser = auth.currentUser!!

        builder.setView(view)
        builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
            //Toast.makeText(this, "Successfully delete this account", Toast.LENGTH_SHORT).show()
            toUser.delete()
                .addOnCompleteListener {
                    Toast.makeText(this, "Successfully delete this account", Toast.LENGTH_SHORT).show()
                }
            reference.removeValue()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->  })
        builder.show()*/
    }
}

//class FriendItem(val user: FriendList): Item<ViewHolder>(){
//

class FriendItem(val user: User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_friend_list.text = user.username

        Picasso.get().load(user.profileImage).into(viewHolder.itemView.imageView_friend_list)
    }

    override fun getLayout(): Int {
        return R.layout.friend_list_row
    }
}
