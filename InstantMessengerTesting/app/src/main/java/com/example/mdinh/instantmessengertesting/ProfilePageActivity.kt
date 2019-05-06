package com.example.mdinh.instantmessengertesting

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_profile_page.*

class ProfilePageActivity : AppCompatActivity() {
    private val TAG = "ProfilePageActivity"

    var logged_user = RecentMessagesActivity.logged_user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        Log.d(TAG, logged_user?.username)

        usernameDisplayProfile_textview.text = logged_user?.username
        emaildisplayProfile_textview.text = logged_user?.email_address
        Picasso.get().load(logged_user?.profileImageUrl).into(photodisplayProfile_circleview)

        signoutProfile_button.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        })
    }

}
