package com.mindfulness.googlenewsembed.repositories

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.mindfulness.googlenewsembed.data.entities.DataResult
import com.mindfulness.googlenewsembed.data.entities.news.Articles
import com.mindfulness.googlenewsembed.data.entities.response.ArticlesResponse
import com.mindfulness.googlenewsembed.room_database.ArticleDatabase
import io.reactivex.Single

class ArticleRepository(var context: Context,private val articleSource: ArticleSource) {

    //fun getNewsAllDataApi(context: Context) = articleSource.getNewsAllDataApi(context)
    @RequiresApi(Build.VERSION_CODES.O)
    fun GetNewsData():LiveData<DataResult<ArticlesResponse>> = articleSource.GetNewsData()

}