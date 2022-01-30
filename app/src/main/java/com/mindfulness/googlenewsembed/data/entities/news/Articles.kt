package com.mindfulness.googlenewsembed.data.entities.news

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "Articles_db3")
data class Articles(

    //var ArticlesId: Long?=null,
    var author: String? = null,
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var urlToImage: String? = null,
    var publishedAt: String? = null,
    var content: String? = null,
    val publishedAtDate: Date?,
    var ArticlesImage: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var articlesId: Long = 0
    constructor() : this("", "", "", "", "", "", "", null, "")
}

