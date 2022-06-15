package com.example.tpnewsapp.NewsCategories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tpnewsapp.Adapters.SavedNewsAdapter
import com.example.tpnewsapp.LoginRegister.LoginActivity
import com.example.tpnewsapp.Models.Articles
import com.example.tpnewsapp.Profile.ProfileActivity
import com.example.tpnewsapp.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.news_view.*

class SavedNewsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_news)

        verifyUserIsLoggedIn()

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
        textView.text = "Saved News"

        val currentUser = FirebaseAuth.getInstance().currentUser
        var uid: String? = null
        val firebaseInstance = FirebaseDatabase.getInstance()
        if (currentUser != null){
            uid = currentUser.uid
        }
        val firebasedatabase = firebaseInstance!!
            .getReference("/Users/${uid}/article")

        val eventListener: ValueEventListener
        eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val articlArray = ArrayList<Articles>()
                for(data in dataSnapshot.children){
                    val articles = data.getValue(Articles::class.java)
                    articlArray.add(articles as Articles)
                    println("DATA KEY"+data.key)

                    removeButton?.setOnClickListener {
                        val remove = firebaseInstance!!.getReference("Users/${uid}/article/${data.key}")
                        remove.setValue(null)
                    }
                }
                if(articlArray.size > 0){
                    news_gridView. adapter =
                        SavedNewsAdapter(
                            this@SavedNewsActivity,
                            articlArray
                        )
                }
              //  news_gridView. adapter = SavedNewsAdapter(this, articlArray)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        firebasedatabase.addListenerForSingleValueEvent(eventListener)

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
            }

            R.id.Profile -> {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    ProfileActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Technology -> {
                Toast.makeText(this, "Technology", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    TechNewsActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.SavedNews -> {
                Toast.makeText(this, "Saved News", Toast.LENGTH_SHORT).show()

            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
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
