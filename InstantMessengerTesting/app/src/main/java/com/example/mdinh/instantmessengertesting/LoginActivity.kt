package com.example.mdinh.instantmessengertesting

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginLogin_button.setOnClickListener{
            val email = emailLogin_edittext.text.toString()
            val password = passwordLogin_edittext.text.toString()

            Log.d("LoginActivity", "Email: " + email)
            Log.d("LoginActivity", "Password: + $password")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(it.isSuccessful) {
                        Log.d("LoginActivity", "Login Successful: ${it.result?.user?.uid}") //What does ?. mean?

                        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()

                        val intent =  Intent(this, RecentMessagesActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    else return@addOnCompleteListener
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                    Log.d("LoginActivity", "Failed to login. ${it.message}")
                }
        }

        donthaveaccountLogin_textview.setOnClickListener{
            Log.d("MainActivity", "Accessing registration screen for new users.")
            finish();
        }
    }
}