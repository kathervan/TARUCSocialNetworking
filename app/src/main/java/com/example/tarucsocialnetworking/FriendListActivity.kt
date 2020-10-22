package com.example.tarucsocialnetworking

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.activity_interest_field.*
import kotlinx.android.synthetic.main.user_row_friend_list.view.*

class FriendListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        //val adapter = GroupAdapter<ViewHolder>()

        //adapter.add(UserItem())
        //recyclerview_friendlist.adapter = adapter

        recyclerview_friendlist.adapter
        recyclerview_friendlist.layoutManager = LinearLayoutManager(this)

        fetchUsers()
    }

    private fun fetchUsers(){
        FirebaseDatabase.getInstance().getReference("/users")
            .ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val adapter = GroupAdapter<ViewHolder>()
                    snapshot.children.forEach{
                        Log.d("FriendList", it.toString())
                        val user = it.getValue(User::class.java)
                        if (user != null) {
                            adapter.add(UserItem(user))
                        }
                    }

                    adapter.setOnItemClickListener{item, view->
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        startActivity(intent)

                        finish()
                    }

                    recyclerview_friendlist.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }
}

class UserItem(val user: User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_friend_list.text = user.username

        Picasso.get().load(user.profileImage).into(viewHolder.itemView.imageView_friend_list)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_friend_list
    }
}
