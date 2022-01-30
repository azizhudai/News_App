package com.mindfulness.googlenewsembed.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mindfulness.googlenewsembed.data.entities.DataResult
import com.mindfulness.googlenewsembed.data.entities.news.Articles
import com.mindfulness.googlenewsembed.data.entities.response.ArticlesResponse
import com.mindfulness.googlenewsembed.helper.Helper
import com.mindfulness.googlenewsembed.helper.ImageBitmapString
import com.mindfulness.googlenewsembed.helper.ImageBitmapString.ImageUrlToBitmap
import com.mindfulness.googlenewsembed.interfaces.IResultListener
import com.mindfulness.googlenewsembed.repositories.ArticleRepository
import com.mindfulness.googlenewsembed.room_database.ArticleDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*

class MainViewModel(
    private val context: Context,
    private val repository: ArticleRepository
) : ViewModel() {

    var resultListener: IResultListener? = null

    //disposable to dispose the Completable
    private val disposables = CompositeDisposable()

    private var dataBaseInstance: ArticleDatabase? = null

    var personsList = MutableLiveData<List<Articles>>()
    var articleList = MutableLiveData<List<Articles>>()

    // private val _articleList: LiveData<List<Articles>> = MutableLiveData()

    //fun articleList() :LiveData<List<Articles>> =
    var articleListLiveData = MutableLiveData<List<Articles>>()

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    fun setInstanceOfDb(dataBaseInstance: ArticleDatabase) {
        this.dataBaseInstance = dataBaseInstance
    }

    fun clearArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            dataBaseInstance?.getArticleDao()?.clearArticle()
        }
    }

    /*fun listArticle(){
        dataBaseInstance?.getArticleDao()?.getAllArticles()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({

            })
    }*/

    fun getAllDbArticle(){

    }

    fun getDBArticle() {
        //viewModelScope.launch {  }
        //return repository.getAllArticle()
        resultListener?.onStarted()

        dataBaseInstance?.getArticleDao()?.getAllArticles()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                if (!it.isNullOrEmpty()) {
                    personsList.postValue(it)
                    resultListener?.onSuccess()
                } else {
                    resultListener?.onFailure("No Records Data")
                    personsList.postValue(listOf())
                }
                it?.forEach {

                }
            }, {
            })?.let {
                compositeDisposable.add(it)
            }
    }

    fun getAllArticleDb(): Articles? {

        return dataBaseInstance?.getArticleDao()?.getLastArticle()
    }

    /*
        fun getNewsData() {
            resultListener?.onStarted()

            val disposable = repository.getNewsAllDataApi(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    try {

                        resultListener?.onSuccess()
                    } catch (ex: Exception) {
                        resultListener?.onFailure(ex.localizedMessage!!)
                    }
                }, {
                    resultListener?.onFailure(it.localizedMessage!!)
                })
            disposables.add(disposable)
        }
    */
    @RequiresApi(Build.VERSION_CODES.O)
    fun GetNewsData(): LiveData<DataResult<ArticlesResponse>> {
        // resultListener?.onStarted()
        val result = viewModelScope.async {
            repository.GetNewsData()
        }
        return runBlocking { result.await() }
    }


    fun ConvertUrmImageToBitmapAllNews(
        context: Context,
        articlesResponse: List<ArticlesResponse>?
    ): MutableLiveData<List<Articles>> {//: MutableLiveData<DataResult<ArticlesResponse>>?
        var article = Articles()
        val articleDatabase = ArticleDatabase.invoke(context)
        var articleListArray: List<Articles> = ArrayList<Articles>()
        Log.i("articleListObserveList", articlesResponse?.get(0)!!.title)
        if (!articlesResponse.isNullOrEmpty()) {
            val getArticleArray = getAllArticleDb()
            for (item in articlesResponse) {

                //compare new data and db data
                val convertNewDataDate = item.publishedAt?.let { Helper.ZonedDateTimeToDate(it) }
                article =
                    Articles(
                        item.author,
                        item.title,
                        item.description,
                        item.url,
                        item.urlToImage,
                        item.publishedAt,
                        item.content,
                        convertNewDataDate,
                        ""
                    )
                articleListArray.toMutableList().add(article)
                //first insert db
                if (getArticleArray == null) {
                    SaveDbArticle(articleDatabase, article)
                } else {
                    //var convertDBDataLastDate = item.publishedAt?.let { Helper.ZonedDateTimeToDate(it) }
                    if (convertNewDataDate != null) {
                        if (convertNewDataDate > getArticleArray.publishedAtDate) {
                            SaveDbArticle(articleDatabase, article)
                        }
                    }
                }
            }
            articleListLiveData.postValue(articleListArray)
        }
        return articleListLiveData
    }

    fun SaveDbArticle(articleDatabase: ArticleDatabase, article: Articles) {
        viewModelScope.async {
            articleDatabase.getArticleDao().insertArticle(article)
        }
    }

    fun ImageUrlToBitmap(context: Context, articlesResponse: ArticlesResponse): Bitmap? {
        var imageBitmap: Bitmap? = null
        try {
            imageBitmap = Glide.with(context)
                .asBitmap()
                .load(articlesResponse.urlToImage).submit().get()
        } catch (ex: Exception) {
            imageBitmap = null
        }
        if (imageBitmap == null)
            Log.i("imageBitmap", "imageBitmap boş geldi")

        return imageBitmap
    }

    //disposing the disposables
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}