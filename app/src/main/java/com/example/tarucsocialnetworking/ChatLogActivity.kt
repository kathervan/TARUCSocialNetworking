package com.example.tarucsocialnetworking

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tarucsocialnetworking.InterestFieldActivity.Companion.currentUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.coroutines.*
import java.sql.Time

class ChatLogActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    companion object {
        val TAG = "ChatLog"
        var muteCount = false
    }

    val adapter = GroupAdapter<ViewHolder>()

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationId = 101

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerView_chat_log.adapter = adapter

        //val username = intent.getStringExtra(FriendListActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(FriendListActivity.USER_KEY)
        //toUser = intent.getParcelableExtra<User>(UserListActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

        listenForMessages()

        send_button_chat_log.setOnClickListener{
            Log.d(TAG, "Attempt")
            performSendMessage()
        }

        createNotificationChannel()
    }

    var toUser: User? = null

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if(chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    val currentUser = currentUser ?: return

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    }else{
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
        })
    }

    private fun performSendMessage(){
        val text = editText_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        //val user = intent.getParcelableExtra<User>(FriendListActivity.USER_KEY)
        val user = intent.getParcelableExtra<User>(UserListActivity.USER_KEY)
        val toId = user.uid


        if (text.isEmpty()) return

        if(fromId == null) return
        sendNotification()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                editText_chat_log.text.clear()
                recyclerView_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_0_1){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){
        val text = editText_chat_log.text.toString()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if(!muteCount){
            with(NotificationManagerCompat.from(this)){
                notify(notificationId, builder.build())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_add -> {
                val uri = toUser!!.profileImage
                val username = toUser!!.username
                val ref = FirebaseDatabase.getInstance().getReference("users-friend").child(toUser!!.uid)
                //val friendList = FriendList(uid, toUsername, uri)
                val friendList =  User(toUser!!.uid, username,uri, toUser!!.userPhoneNo)

                ref.setValue(friendList)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully add to friend list", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Fail to add friend from friend list", Toast.LENGTH_SHORT).show()
                    }

                val intent = Intent(this, InterestFieldActivity::class.java)
                startActivity(intent)

            }
            R.id.menu_remove -> {
                val toId = toUser!!.uid
                FirebaseDatabase.getInstance().getReference("users-friend").child(toId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Successfully remove from friend list", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Fail to remove friend from friend list", Toast.LENGTH_SHORT).show()
                    }

                val intent = Intent(this, InterestFieldActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_Friend_List -> {
                val intent = Intent(this, FriendListActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_mute -> {
                val uid = FirebaseAuth.getInstance().uid ?: ""
                val reference = FirebaseDatabase.getInstance().getReference("/users/$uid");
                val builder = AlertDialog.Builder(this)
                builder.setTitle("")
                val view = layoutInflater.inflate(R.layout.mute_notification, null)

                builder.setView(view)
                builder.setPositiveButton("Mute", DialogInterface.OnClickListener { _, _ ->
                    Toast.makeText(this, "Successful Mute", Toast.LENGTH_SHORT).show()
                    muteCount = true
                })
                builder.setNegativeButton("Un-Mute", DialogInterface.OnClickListener { _, _ ->
                    Toast.makeText(this, "Successful Un-Mute", Toast.LENGTH_SHORT).show()
                    muteCount = false
                })
                builder.show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

class ChatFromItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text

        val uri = user.profileImage
        val targetImageView = viewHolder.itemView.imageView_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text

        //load our user image into the star
        val uri = user.profileImage
        val targetImageView = viewHolder.itemView.imageView_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}
/*
@Parcelize
class FriendList(val uid:String, val username: String, val profileImage: String): Parcelable{
    constructor() : this("","","")
}*/