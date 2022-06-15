package com.example.tpnewsapp.Search

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.example.tpnewsapp.Adapters.MainAdapter
import com.example.tpnewsapp.Models.ArticleFeed
import com.example.tpnewsapp.NewsCategories.MainActivity
import com.example.tpnewsapp.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.*
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolBar)

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.selectAll()


        //Making the abilty to listen to when text change in search bar
        val searchText = findViewById<EditText>(R.id.search_edit_text)
        searchText.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?) {

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJson(s.toString())
            }

        })

        searchText.setOnEditorActionListener{s, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                searchJson(s.text.toString())
                true
            } else {
                false
            }
        }


        // Verify the action and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doMySearch(query)
            }
        }
    }

    /*
 * This method fetches the information from the online News API
 * */
    fun searchJson(text: String){
        println("searching Json")

        var url = "https://newsapi.org/v2/everything?q=pvxw&" +
                "apiKey=e3811362a9de4ebab332970b34daf942"

        //pvxw is a combination that is very unlikely to be used in that order
        url = url.replace("pvxw",text)
        if(text == ""){
           Log.d("Search","Nothing to Search")
        }else{
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
                    news_gridViewSearch.adapter =
                        MainAdapter(this@SearchActivity, articleFeed)
                }
            }
            //when it fails to call Json
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute request")
            }
        })
        }
    }

    private fun doMySearch(query: String) {}
}