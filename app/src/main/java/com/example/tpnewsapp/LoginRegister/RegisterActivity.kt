package com.example.tpnewsapp.LoginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.tpnewsapp.Models.ArticleFeed
import com.example.tpnewsapp.Models.Articles
import com.example.tpnewsapp.Models.Source
import com.example.tpnewsapp.NewsCategories.MainActivity
import com.example.tpnewsapp.R
import com.example.tpnewsapp.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var prefTextView : TextView
    lateinit var spinner: Spinner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        spinner = findViewById(R.id.spinner1) as Spinner
        prefTextView = findViewById(R.id.option_text1) as TextView

        val options = arrayOf("Business", "Entertainment", "Health", "Science",
            "Sports", "Technology", "Games", "Movies", "Football", "Basketball")

        spinner.adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,
            options)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //textView.text = "Select News Preference"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int,
                                        id: Long) {
                prefTextView.text = options.get(position)

            }

        }

        // register button listener
        register_button.setOnClickListener {
            registerUser()

        }

        //already have account on click listener
        already_have_account_textView.setOnClickListener {
            Toast.makeText(this, "Login to your account!", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    fun registerUser(){
        val email = email_editText.text.toString()
        val password = password_editText.text.toString()
        val username  = username_editText.text.toString()
        val preference = prefTextView.text.toString()

        //get user information from this activity to transfer to other activities.
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("USERNAME", username)
        intent.putExtra("PREFERENCE", preference)

        if(email.isEmpty() || password.isEmpty()|| username.isEmpty()|| preference.isEmpty()){
            Toast.makeText(this, "Please Enter Email/Password",
                Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity", "Email is: " + email)
        Log.d("MainActivity", "Password is: $password")

        //Firebase authentication to create user profile
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if successful
                Log.d("Main", "Successfully created user with uid:" +
                        "${it.result?.user?.uid}")
                Toast.makeText(this, "Welcome "+username_editText.text.toString(),
                    Toast.LENGTH_SHORT).show()
                saveUserToFireBase()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user:" +
                        " ${it.message}", Toast.LENGTH_SHORT).show()

            }


    }


    // saves a user in the data base
    private fun saveUserToFireBase(){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/${uid}")
        val id = ""
        val name =""
        val author = ""
        val title = ""
        val description =""
        val url = ""
        val urlToImage = ""
        val publishedAt =""
        val content =""


        val source = Source(id,name)

        val articles = Articles(source, author, title,  description, url,  urlToImage, publishedAt, content)
        val articleFeed =""//ArticleFeed(listOf(articles))
        val user = User(
            uid, username_editText.text.toString(), email_editText.text.toString(),
            prefTextView.text.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Main", "User added to DataBase")
                //open activity or the user to select their preferences.
            }
       // ref.setValue(articleFeed)

    }


}