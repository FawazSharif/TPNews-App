package com.example.tpnewsapp.NewsCategories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.example.tpnewsapp.LoginRegister.LoginActivity
import com.example.tpnewsapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_news_site.*
import kotlinx.android.synthetic.main.news_content.*

class NewsSiteActivity : AppCompatActivity(){

    lateinit var toolbar: Toolbar
    lateinit var textview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_site)

        verifyUserIsLoggedIn()

        val intent: Intent = getIntent()

        val navBarTitle = intent.getStringExtra("SOURCE_KEY")

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = navBarTitle


        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        textview = findViewById(R.id.newsSiteName)
        textview.text = navBarTitle


        val newsSiteLink = intent.getStringExtra("NEWS_SITE_KEY")

        //loads the source URL to the webView in source_content.xml
        newsSiteView.loadUrl(newsSiteLink)

        newsSiteView.settings.javaScriptEnabled = true
        newsSiteView.settings.loadWithOverviewMode = true
        newsSiteView.settings.useWideViewPort = true

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