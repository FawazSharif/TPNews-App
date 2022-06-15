package com.example.tpnewsapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.tpnewsapp.Models.Articles
import com.example.tpnewsapp.NewsCategories.NewsSiteActivity
import com.example.tpnewsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


/*
   * Adapter class to populate the textviews and imagesView With data.
   * takes Context and Articlefeed class as args
   * ArticleFeed takes a List<articles> as args
   * */
class SavedNewsAdapter (val context: Context, val articleFeed: ArrayList<Articles>): BaseAdapter(){
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
        saveButton?.visibility = View.GONE

        source?.text = articleFeed.get(position).source?.name
        articleName?.text = articleFeed.get(position).title
        authorName?.text = articleFeed.get(position).author
        date?.text = articleFeed.get(position).publishedAt
        description?.text = articleFeed.get(position).description



        var thumbnailImageView = articleFeed.get(position).urlToImage
        /*
        * The API does not always have an image so this will check for image and place a
        * different image in the image view
        * */
        if(thumbnailImageView == ""){
            thumbnailImageView = "https://catholicnewstt.com/wp-content/uploads/2017/05/" +
                    "no-image-available.jpg"
        }
        Picasso.with(view?.context).load(thumbnailImageView).into(newsImage)
        val newsSiteUrl = articleFeed.get(position).url
        val sourceName = articleFeed.get(position).source?.name

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
            .getReference("/Users/${uid}")
        val postkey = firebasedatabase.key!!

        val q =   firebaseInstance.getReference("Users/eVXxDe196RNtvkqjUaoBkM3zcJ93/article")
            .orderByChild("urlToImage").equalTo(articleFeed.get(position).description)

        val postsRef: DatabaseReference = firebasedatabase.child(uid!!)
        val articleID = postsRef.key
        println("HERE AGAIN " + articleID)


        removeButton?.setOnClickListener {
            val eventListener: ValueEventListener
            eventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.getValue()




                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
          q.addListenerForSingleValueEvent(eventListener)



           /*
           firebaseInstance.getReference("Users/eVXxDe196RNtvkqjUaoBkM3zcJ93/article")
                .orderByChild("urlToImage").equalTo(articleFeed.get(position).description).ref.removeValue()
            println("HERE:  "+   firebaseInstance.getReference("Users/eVXxDe196RNtvkqjUaoBkM3zcJ93/article")
                .orderByChild("urlToImage").equalTo(articleFeed.get(position).description))
            Toast.makeText(context, "Removed from Saved", Toast.LENGTH_SHORT).show()
            */

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
        return articleFeed.get(position)
    }

    override fun getItemId(position: Int): Long {
        //To change body of created functions use File | Settings | File Templates.
        return position.toLong()
    }

    override fun getCount(): Int {
        //To change body of created functions use File | Settings | File Templates.
        return articleFeed.count()
    }


}