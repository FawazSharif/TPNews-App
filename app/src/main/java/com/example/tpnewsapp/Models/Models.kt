package com.example.tpnewsapp.Models

class Sources(val id : String, val name: String, val description: String, val url: String,
              val category: String, val language: String, val country: String){
    constructor(): this("","","","","","","")
}

class SourceFeed(val sources: List<Sources>)

class Source(val id: String?, val name : String? ){
    constructor(): this("","")
}

class ArticleFeed(val articles: List<Articles>){
    constructor(): this( listOf<Articles>(Articles(Source("",""),
        "","","","","","","")))
}


class User(val uid: String, val username: String, val email: String, val preference: String /*val articleFeed: ArticleFeed*/){
    constructor() : this("","","", ""
       /* ArticleFeed(listOf<Articles>(Articles(Source("",""),
        "","","","","","","")))*/)

}

class Articles(val source: Source?, val author: String?, val title: String?, val description: String?,
               val url: String?, val urlToImage: String?, val publishedAt: String?, val content: String?){
    constructor(): this(Source("",""),
        "","","","","","","")


}
