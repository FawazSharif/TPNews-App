package com.example.tpnewsapp.NewsCategories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tpnewsapp.LoginRegister.LoginActivity
import com.example.tpnewsapp.Adapters.MainAdapter
import com.example.tpnewsapp.Models.ArticleFeed
import com.example.tpnewsapp.Models.User
import com.example.tpnewsapp.Profile.ProfileActivity
import com.example.tpnewsapp.R
import com.example.tpnewsapp.Search.SearchActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header.*
import okhttp3.*
import java.io.IOException

class TechNewsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyUserIsLoggedIn()

        fetchJson()

        toolbar = findViewById(R.id.toolbar)
        // setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        textView = findViewById(R.id.name)
        textView.text = "Tech News"

        /*
        getting the reference of the user from the firebase database, to access
        the users information
        */
        var uid: String? = null
        val firebaseInstance = FirebaseDatabase.getInstance()
        val firebasedatabase = firebaseInstance!!.getReference("/Users")
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null){
            uid = currentUser.uid
        }

        //User data change Listener
        firebasedatabase!!.child(uid!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                //check if null
                if (user == null){
                    Log.d("user null check", "user data is null")
                    return
                }
                welcomeUserText.text = "HELLO "+ user.username
                userEmailText.text = user.email

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        searchButton.setOnClickListener {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,
                SearchActivity:: class.java)
            this.startActivity(intent)
        }


    }

    /*
   * Method to change activity when an item from the navigation drawer is selected
   * */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.topStories -> {
                Toast.makeText(this, "Top Stories", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Business -> {
                Toast.makeText(this, "Business", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BusinessNewsActivity:: class.java)
                this.startActivity(intent)

            }

            R.id.Entertainment -> {
                Toast.makeText(this, "Entertainment", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EntertainmentNewsActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Health -> {
                Toast.makeText(this, "Health", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    HealthNewsActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Science -> {
                Toast.makeText(this, "Science", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    ScienceNewsActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Sports -> {
                Toast.makeText(this, "Sports", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    SportsNewsActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Technology -> {
                Toast.makeText(this, "Technology", Toast.LENGTH_SHORT).show()
            }

            R.id.SavedNews -> {
                Toast.makeText(this, "Saved News", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    SavedNewsActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Profile -> {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    ProfileActivity:: class.java)
                this.startActivity(intent)
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /*
   * This method fetches the information from the online News API
   * */
    fun fetchJson(){
        println("fetching Json")

        val url = "https://newsapi.org/v2/top-headlines?country=gb&category=technology&" +
                "apiKey=e3811362a9de4ebab332970b34daf942"

        val  request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request). enqueue(object: Callback {

            //This is what happens on response when fetching the Json
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                println(body)

                val gson = GsonBuilder().create()

                val articleFeed = gson.fromJson(body, ArticleFeed::class.java)

                runOnUiThread {
                    news_gridView.adapter =
                        MainAdapter(
                            this@TechNewsActivity,
                            articleFeed
                        )
                }

            }

            //when it fails to call Json
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
    }

    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}