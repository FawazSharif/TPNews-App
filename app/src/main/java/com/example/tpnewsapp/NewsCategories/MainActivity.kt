package com.example.tpnewsapp.NewsCategories

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tpnewsapp.LoginRegister.LoginActivity
import com.example.tpnewsapp.Adapters.MainAdapter
import com.example.tpnewsapp.Models.ArticleFeed
import com.example.tpnewsapp.Models.Articles
import com.example.tpnewsapp.Models.User
import com.example.tpnewsapp.Notification.NotificationReceiver
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
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var textView: TextView
    lateinit var imageView: ImageView

    private val CHANNEL_ID = "channel_id"
    private val notificationId = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        verifyUserIsLoggedIn()

        createNotificationChannel()

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

        searchButton.setOnClickListener {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
            val intent = Intent(this,
                SearchActivity:: class.java)
            this.startActivity(intent)
        }




       /*
       * Here we set the time of day and how many times to repeat the notification everyday.
       * */

        val currentTime = System.currentTimeMillis()
         val tenSeconds = 1000*2
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val intent =Intent(applicationContext, NotificationReceiver::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, notificationId, intent, 0)
        val alarmManger: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManger.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_HALF_DAY,pendingIntent)

    }


    //Ends the application when the back button is pressed from main activity
    override fun onBackPressed() {
        super.onBackPressed()
        System.exit(0)
    }

    /*
    * Method to change activity when an item from the navigation drawer is selected
    * */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.topStories -> {
                Toast.makeText(this, "Top Stories", Toast.LENGTH_SHORT).show()
            }

            R.id.Business -> {
                Toast.makeText(this, "Business", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BusinessNewsActivity:: class.java)
                this.startActivity(intent)
            }

            R.id.Entertainment -> {
                Toast.makeText(this, "Entertainment", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,
                    EntertainmentNewsActivity:: class.java)
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
                val intent = Intent(this, ScienceNewsActivity:: class.java)
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
                val intent = Intent(this,
                    TechNewsActivity:: class.java)
                this.startActivity(intent)
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
   * This method fetches the information in JSON format from the online News API
   * */
    fun fetchJson(){
        println("fetching Json")

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
        firebasedatabase!!.child(uid!!).addValueEventListener(object: ValueEventListener{
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
               val articleFeed =  snapshot.getValue(ArticleFeed::class.java)
                val articles =snapshot.getValue(Articles::class.java)

                //check if user is null
                if (user == null){
                    Log.d("user null check", "user data is null")
                    return
                }
                val url = "https://newsapi.org/v2/top-headlines?q="+user.preference+"&language=en"+
                        "&apiKey=e3811362a9de4ebab332970b34daf942"
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
                                    this@MainActivity,
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
            override fun onCancelled(error: DatabaseError) {

            }
        })

    }



    //Checks if a user is logged in and sends them to the register activity if no one is logged in
    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null){
            Log.d("null user", "Userid: "+uid)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Log.d("intent","Intent is working ")
        }else{
            fetchJson()
        }
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "NEWS Alet"
            val descriptionText ="new news buddy check me out lol"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel  = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

}