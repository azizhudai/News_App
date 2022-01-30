package com.mindfulness.googlenewsembed.data.entities.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

open class JsonResultData(val status: String, val totalResults: Int, val articles: List<ArticlesResponse>) {

}

open class ArticlesResponse(val source: Source?,
                            val author: String?,
                            val title: String,
                            val description: String?,
                            val url: String?,
                            val urlToImage: String?,
                            val publishedAt: String?,
                            val content: String?,
                            val urlImageBase64:String?=null
)

class Source(val id: String?, val name: String?) {

}
@JsonIgnoreProperties(ignoreUnknown = true)
class Data(
    val author: String?,
    val title: String,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)