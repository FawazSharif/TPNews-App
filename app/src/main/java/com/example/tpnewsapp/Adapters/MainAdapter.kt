package com.example.tpnewsapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.M
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.tpnewsapp.Models.ArticleFeed
import com.example.tpnewsapp.Models.Articles
import com.example.tpnewsapp.Models.Source
import com.example.tpnewsapp.NewsCategories.NewsSiteActivity
import com.example.tpnewsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


/*
* Adapter class to populate the textviews and imagesView With data.
* takes Context and Articlefeed class as args
* ArticleFeed takes a List<articles> as args
* */
class MainAdapter (val context: Context, val articleFeed: ArticleFeed): BaseAdapter(){

    var isSaved = true

    var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //To change body of created functions use File | Settings | File Templates.
        var view = convertView
        if(view == null){
            view = layoutInflater.inflate(R.layout.news_view, parent,false)
        }

        // To find the textViews and ImageViews in the recipe_page.xml file
        val newsImage = view?.findViewById<ImageView>(R.id.newsImage)
        val source = view?.findViewById<TextView>(R.id.sourceName)
        val articleName = view?.findViewById<TextView>(R.id.articleTitle)
        val authorName = view?.findViewById<TextView>(R.id.author)
        val date = view?.findViewById<TextView>(R.id.dateAndTime)
        val description = view?.findViewById<TextView>(R.id.description)
        val linearlayout = view?.findViewById<LinearLayout>(R.id.linearLayout)
        val shareButton = view?.findViewById<com.google.android.material
        .floatingactionbutton.FloatingActionButton>(R.id.shareButton)
        val removeButton = view?.findViewById<com.google.android.material
        .floatingactionbutton.FloatingActionButton>(R.id.removeButton)

        val saveButton =view?.findViewById<com.google.android.material.
        floatingactionbutton.FloatingActionButton>(R.id.saveButton)

        source?.text = articleFeed.articles?.get(position).source?.name
        articleName?.text = articleFeed.articles?.get(position).title
        authorName?.text = articleFeed.articles?.get(position).author
        date?.text = articleFeed.articles?.get(position).publishedAt
        description?.text = articleFeed.articles?.get(position).description

        var thumbnailImageView = articleFeed.articles.get(position).urlToImage
        /*
        * The API does not always have an image so this will check for image and place a
        * different image in the image view
        * */
        if(thumbnailImageView == ""){
            thumbnailImageView = "https://catholicnewstt.com/wp-content/uploads/2017/05/" +
                    "no-image-available.jpg"
        }
        Picasso.with(view?.context).load(thumbnailImageView).into(newsImage)

        val newsSiteUrl = articleFeed.articles.get(position).url
        val sourceName = articleFeed.articles.get(position).source?.name

        val articleTitle = articleFeed.articles.get(position).title

        val content = articleFeed.articles.get(position).content
        // removeButton?.visibility = View.GONE


        //Share button on click listener.
        shareButton?.setOnClickListener {
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, "Check out this news from TPNews: "+newsSiteUrl)
                this.type = "text/plain"
            }
            context.startActivity(shareIntent)
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        var uid: String? = null
        val firebaseInstance = FirebaseDatabase.getInstance()
        if (currentUser != null){
            uid = currentUser.uid
        }

        val firebasedatabase = firebaseInstance!!
            .getReference("/Users/${uid}").child("article")
        firebasedatabase.key

        val postkey = firebasedatabase.key!!
        println("KEYYY" + firebasedatabase)



         removeButton?.visibility = View.GONE

        saveButton?.setOnClickListener {

            val eventListener: ValueEventListener
            eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(isSaved == true){
                            isSaved = false
                            val source = Source(articleFeed.articles?.get(position).source?.id,
                                articleFeed.articles?.get(position).source?.name)
                            val author = articleFeed.articles?.get(position).author
                            val title = articleFeed.articles?.get(position).title
                            val description = articleFeed.articles?.get(position).description
                            val url = articleFeed.articles.get(position).url
                            val urlToImage = articleFeed.articles.get(position).urlToImage
                            val publishedAt = articleFeed.articles?.get(position).publishedAt
                            val content = articleFeed.articles?.get(position).content
                            saveButton.visibility = View.GONE
                            saveArticleToFireBase(source,author,title,description,url,urlToImage,publishedAt,content)
                            Toast.makeText(context, "Added To Saved News", Toast.LENGTH_SHORT).show()
                    }else if(isSaved == false){
                        saveButton.visibility = View.VISIBLE

                    }

                }


                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            firebasedatabase.addListenerForSingleValueEvent(eventListener)

        }



        //Open webPage for the source
        linearlayout?.setOnClickListener{
            val intent = Intent(context, NewsSiteActivity::class.java)
            intent.putExtra("NEWS_SITE_KEY", newsSiteUrl)
            intent.putExtra("SOURCE_KEY", sourceName)

            context.startActivity(intent)
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        //To change body of created functions use File | Settings | File Templates.
        return articleFeed.articles[position]
    }

    override fun getItemId(position: Int): Long {
        //To change body of created functions use File | Settings | File Templates.
        return position.toLong()
    }

    override fun getCount(): Int {
        //To change body of created functions use File | Settings | File Templates.
        return articleFeed.articles.count()
    }

    // This function will save a user's Article to firebase
    private fun saveArticleToFireBase(source: Source?, author: String?, title: String?, description: String?,
                                      url: String?, urlToImage: String?, publishedAt:String?, content: String?){
        val uid = FirebaseAuth.getInstance().uid?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/${uid}/article")
        val updates: MutableMap<String, Articles> = HashMap()

        val articles = Articles(source, author, title,  description, url,  urlToImage, publishedAt, content)
        val articleFeed = ArticleFeed(listOf(articles))
        ref.push().setValue(articles)
           println("REF KEYYY::: "+ ref.key)


    }


}