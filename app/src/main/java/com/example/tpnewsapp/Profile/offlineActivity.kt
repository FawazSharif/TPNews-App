package com.example.tpnewsapp.Profile

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class offlineActivity : Application() {
   override fun onCreate(){
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }


}