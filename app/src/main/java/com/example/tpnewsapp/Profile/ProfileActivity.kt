package com.example.tpnewsapp.Profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tpnewsapp.LoginRegister.LoginActivity
import com.example.tpnewsapp.Models.User
import com.example.tpnewsapp.NewsCategories.MainActivity
import com.example.tpnewsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    lateinit var prefTextView : TextView
    lateinit var spinner: Spinner
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        spinner = findViewById(R.id.changePreferenceSpinner) as Spinner
        prefTextView = findViewById(R.id.changePreferenceOptions) as TextView

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

        /*
        getting the reference of the user from the firebase database, to access
        the users information
        */


        var uid: String? = null
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebasedatabase = firebaseInstance!!.getReference("/Users")
        firebasedatabase.keepSynced(true)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            uid = currentUser.uid
        }
        //User data change Listener
        firebasedatabase!!.child(uid!!).addValueEventListener(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                //check if user is null
                if (user == null){
                    Log.d("user null check", "user data is null")
                    return
                }
                userNameText.text = user.username
                emailText.text = user.email
                preferenceText.text  = "Current preference: " + user.preference
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        // sign out of profile button listener
        signOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }

        saveButton.setOnClickListener {
            updateUserToFireBase()
            val intent = Intent(this, MainActivity::class.java)
            Toast.makeText(this, "Preference Updated!",
                Toast.LENGTH_SHORT).show()
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)


        }

    }

    /*
    * This function updates the preference of the user
    * will be called when the save button is clicked
    * */
    private fun updateUserToFireBase(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid?: ""
       // val ref = FirebaseDatabase.getInstance().getReference("/Users/${uid}")
        val ref = FirebaseDatabase.getInstance().reference.child("/Users/${uid}/")
        val updates: MutableMap<String, Any> = HashMap()
        updates["preference"] = prefTextView.text.toString()
        ref.updateChildren(updates)

    }
}