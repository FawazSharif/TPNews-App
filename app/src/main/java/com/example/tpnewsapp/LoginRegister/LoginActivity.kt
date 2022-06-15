package com.example.tpnewsapp.LoginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tpnewsapp.NewsCategories.MainActivity
import com.example.tpnewsapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val intent: Intent = getIntent()

        // register button listener
        login_button.setOnClickListener {
            val email = login_email_editText.text.toString()
            val password = login_password_editText.text.toString()

            Log.d("MainActivity", "Attempting to login with Email?password")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Main", "Successfully signed in")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this.startActivity(intent)


                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to sign in")
                    Toast.makeText(this, "Failed to sign in:" +
                            " ${it.message}", Toast.LENGTH_SHORT).show()

                }

        }

        //already have account on click listener
        create_account_textView.setOnClickListener {
            Toast.makeText(this, "Create an account!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterActivity::class.java)
            this.startActivity(intent)
        }

    }
    //Checks if a user is logged in and sends them to the register activity if no one is logged in
    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid != null){
            Log.d("null user", "Userid: "+uid)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.d("intent","Intent is working ")
        }
    }
}