package com.example.mdinh.instantmessengertesting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registration.*
import android.provider.MediaStore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import java.util.*

//Registration screen
class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        //BEGIN: ClickListener for registration button
        registerRegistration_button.setOnClickListener {
            performRegistration()
        }
        //END: ClickListener for registration button.

        //BEGIN: Accessing the login screen for existing users.
        alreadyhaveaccountRegistration_textview.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            Log.d("RegistrationActivity", "Accessing login screen for existing accounts.")
        }
        //END: Accessing the login screen for existing users.

        //Changes that have not been updated to Github 2/11
        //BEGIN: Photo selection for profile.
        selectphotoRegistration_button.setOnClickListener {
            Log.d("RegistrationActivity", "Show photo selection screen")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0)
        }
        //END: Photo selection for profile.
    }

    //Gets selected photo from onActivityResult function.
    var selectedPhotoUri: Uri? = null

    //BEGIN: Registration function.
    private fun performRegistration() {
        //Converts email and password to string.
        val email = emailRegistration_edittext.text.toString()
        val password = passwordRegistration_edittext.text.toString()

        //If email or password is email check to avoid crash.
        if(email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Missing entry for email/password.", Toast.LENGTH_SHORT).show()
            return
        }
        else if(password.length < 6) {
            Toast.makeText(this, "Invalid password. Password must contain at least 6 characters.", Toast.LENGTH_SHORT).show()
            return
        }

        //Logcat message to display email/password entry
        Log.d("RegistrationActivity", "Email: " + email)
        Log.d("RegistrationActivity", "Password: $password")

        //BEGIN: Firebase Authentication

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("RegistrationActivity", "Registration Successful: ${it.result?.user?.uid}") //What does ?. mean?

                imageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d("RegistrationActivity", "Failed to register user. ${it.message}")
                Toast.makeText(this, "Failed to register user. ${it.message}", Toast.LENGTH_SHORT).show()
            }
        //END: Firebase Authentication
    }
    //END: Registration function.

    //Sets URI of selected image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //requestCode 0 is for photo selection
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //Check what the selected image was.
            Log.d("RegistrationActivity", "Photo is selected.")

            //Gets URI of selected photo
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            //val bitmap_Drawable = BitmapDrawable(bitmap)
            //selectphotoRegistration_button.setBackgroundDrawable(bitmap_Drawable)

            selectphotoRegistration_CircleImageView.setImageBitmap(bitmap)
            selectphotoRegistration_button.alpha = 0f
        }
    }

    //Uploads selected image from registration to Firebase storage
    private fun imageToFirebaseStorage() {
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val reference = FirebaseStorage.getInstance().getReference("/images/$filename")

        reference.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegistrationActivity","Uploading image success: ${it.metadata?.path}")

                //Changes that hasn't been uploaded to Git as of 2/27
                reference.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegistrationActivity", "File location: $it")

                    userToFirebaseDatabase(it.toString())
                }
                //Changes that hasn't been uploaded to Git as of 2/27
            }
            .addOnFailureListener {
                Log.d("RegistrationActivity", "Failed to upload image")
            }
    }

    //Changes that hasn't been uploaded to Git as of 2/27
    //Uploaded user account information into the Firebase database
    private fun userToFirebaseDatabase(profileImageUrl: String) {
        //Gets unique user id from Firebase
        val user_id = FirebaseAuth.getInstance().uid ?: ""

        val reference = FirebaseDatabase.getInstance().getReference("/users/$user_id")

        //Object for user account
        val user_account = UserAccount(user_id, usernameRegistration_edittext.text.toString(), emailRegistration_edittext.text.toString(), profileImageUrl)

        reference.setValue(user_account)
            .addOnSuccessListener {
                Log.d("RegistrationActivity", "Saved user account to Firebase database")

                val intent = Intent(this, RecentMessagesActivity::class.java)
                //The statement below prevents the user from using the back button to return to registration screen
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("RegistrationActivity", "Failed to save user account to Firebase database")
            }
    }
    //Changes that hasn't been uploaded to Git as of 2/27
}

//User account class
@Parcelize
class UserAccount(val user_id: String, val username: String, val email_address: String, val profileImageUrl: String) : Parcelable {
    constructor() : this("", "", "", "")
}