package com.example.mdinh.instantmessengertesting

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_searched_profile_page.*
import kotlinx.android.synthetic.main.nav_header.*

class SearchedProfilePageActivity : AppCompatActivity() {
    var user_receiver: UserAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searched_profile_page)

        //val sender_id = FirebaseAuth.getInstance().uid

        user_receiver = intent.getParcelableExtra<UserAccount>(EmailSearchActivity.USER_KEY)

        /*val reference = FirebaseDatabase.getInstance().getReference("/user-friends/$sender_id").push()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.hasChild("user_id")) {
                    Log.d("Friend", "EXISTS!!!!!")
                    addfriendSearchedProfile_button.setEnabled(false)
                    addfriendSearchedProfile_button.text = "Friend"
                    addfriendSearchedProfile_button.setBackgroundColor(Color.DKGRAY)
                }
                else {

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })*/

        usernamedisplaySearchedProfile_textview.text = user_receiver?.username
        emaildisplaySearchedProfile_textview.text = user_receiver?.email_address
        Picasso.get().load(user_receiver?.profileImageUrl).into(photodisplaySearchedProfile_circleview)

        addfriendSearchedProfile_button.setOnClickListener(View.OnClickListener {
            addFriend()
        })
    }

    private fun addFriend() {
        val sender_id = FirebaseAuth.getInstance().uid

        val receiver = user_receiver
        val receiver_id = receiver?.user_id

        if(sender_id == null) return

        val sending_reference = FirebaseDatabase.getInstance().getReference("/user-friends/$sender_id").push()
        val receiving_reference = FirebaseDatabase.getInstance().getReference("/user-friends/$receiver_id").push()

        sending_reference.setValue(receiver!!)
        receiving_reference.setValue(RecentMessagesActivity.logged_user!!)

        finish()
    }
}


