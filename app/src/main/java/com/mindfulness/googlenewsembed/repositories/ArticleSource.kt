package com.mindfulness.googlenewsembed.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.introspect.VisibilityChecker
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.JsonObject
import com.mindfulness.googlenewsembed.api.ServiceBuilder
import com.mindfulness.googlenewsembed.data.entities.DataResult
import com.mindfulness.googlenewsembed.data.entities.news.Articles
import com.mindfulness.googlenewsembed.data.entities.response.ArticlesResponse
import com.mindfulness.googlenewsembed.helper.ImageBitmapString
import com.mindfulness.googlenewsembed.interfaces.IResultListener
import com.mindfulness.googlenewsembed.room_database.ArticleDatabase
import io.reactivex.Completable
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Response
import java.time.ZonedDateTime
import java.util.*
import kotlin.collections.ArrayList

@Suppress("BlockingMethodInNonBlockingContext")
class ArticleSource {

    lateinit var resultApi: Response<JsonObject>
    lateinit var resultData: Any
    var result: Any? = null
    lateinit var resultJsonObj: JsonObject

    var articles: List<ArticlesResponse>? = null
    lateinit var throwable: Throwable
    val retrofit = ServiceBuilder.retrofitService

    var dataResult = DataResult<ArticlesResponse>("", null)
    private var _newsLiveData: LiveData<DataResult<ArticlesResponse>> = MutableLiveData()

    fun isExistArticleCheck() {

    }

    var resultListener: IResultListener? = null

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun GetNewsData(): LiveData<DataResult<ArticlesResponse>> { //= Completable.create { emitter ->
        //   var dataResult = DataResult<ArticlesResponse>()
        resultListener?.onStarted()
        try {

            CoroutineScope(Dispatchers.IO).launch {

                val response = retrofit.getNewsDataAsync("tr")

                resultApi = response.await()
                if (resultApi.isSuccessful) {
                    withContext(Dispatchers.Main) {

                        result = resultApi.body()
                        resultJsonObj = result as JsonObject;

                        val dataJsonObject = JSONObject(result.toString())//.getJSONObject("data")
                        val isSuccess: String = dataJsonObject.getString("status")

                        if (isSuccess == "ok") {
                            resultData = dataJsonObject.getString("articles")
                            val mapper = jacksonObjectMapper()
                            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                            mapper.setVisibility(
                                VisibilityChecker.Std.defaultInstance().withFieldVisibility(
                                    JsonAutoDetect.Visibility.ANY
                                )
                            )
                            Log.i("resultData", resultData.toString())
                            articles = resultData.toString().let { mapper.readValue(it) }

                            if (articles != null) {
                                dataResult.data = articles // as MutableList<ArticlesResponse>?
                                (_newsLiveData as? MutableLiveData)?.value = dataResult
                                _newsLiveData
                                resultListener?.onSuccess()
                            } else {
                                resultListener?.onFailure("Data is Empty")
                            }
                        } else {
                            throwable = Throwable(resultApi.errorBody().toString())
                            // emitter.onError(throwable)
                        }
                    }
                } else {
                    dataResult.message = resultApi.errorBody().toString()
                    //throwable = Throwable(resultApi.errorBody().toString())
                    //emitter.onError(throwable)
                }
            }
        } catch (ex: Exception) {
            Log.i("Exception", ex.localizedMessage!!)
            //val throwable = Throwable(ex.localizedMessage)
            //emitter.onError(throwable)
            dataResult.message = ex.localizedMessage
        }
        (_newsLiveData as? MutableLiveData)?.value = dataResult
        return _newsLiveData
    }


    // @ExperimentalCoroutinesApi
    /*fun getNewsAllDataApi(context: Context) = Completable.create { emitter ->

        //  var dataResult = DataResult()

        try {
            CoroutineScope(Dispatchers.IO).launch {

                val response = retrofit.getNewsDataAsync("tr")

                resultApi = response.await()
                if (resultApi.isSuccessful) {
                    withContext(Dispatchers.Main) {

                        result = resultApi.body()
                        resultJsonObj = result as JsonObject;

                        val dataJsonObject = JSONObject(result.toString())//.getJSONObject("data")
                        val isSuccess: String = dataJsonObject.getString("status")

                        if (isSuccess == "ok") {
                            resultData = dataJsonObject.getString("articles")
                            val mapper = jacksonObjectMapper()
                            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                            mapper.setVisibility(
                                VisibilityChecker.Std.defaultInstance().withFieldVisibility(
                                    JsonAutoDetect.Visibility.ANY
                                )
                            )
                            Log.i("resultData", resultData.toString())
                            articles = resultData.toString().let { mapper.readValue(it) }
                            if (!articles.isNullOrEmpty()) {
                                //  if (response.isCompleted) {
                                Log.i("articlesSize", articles!!.size.toString())

                                //    artileList = dataResult.data as MutableList<ArticlesResponse>

                                val articleDatabase = ArticleDatabase.invoke(context)

                                // var imageBitmap: Bitmap?
                                var ImageBase64: String = ""
                                var ImageBase64Async: Deferred<Unit>
                                var dataImageFuture: FutureTarget<Bitmap>? = null
                                //var date: Date = Calendar.getInstance().time
//var abi = Articles()
                                var article: com.mindfulness.googlenewsembed.data.entities.news.Articles? =
                                    Articles()
                                //null //com.mindfulness.googlenewsembed.data.entities.news.Articles()
                                var articleList: ArrayList<Articles> =
                                    ArrayList()
                                //var articles:ArrayList<Articles> = ArrayList()//List<Articles>(artileList.size,Articles():artileList.size) //listOf<com.mindfulness.googlenewsembed.data.entities.news.Articles>()
                                var imageBitmap: Bitmap? = null
                                try {
                                    for (item in articles!!) {
                                        //for ((index, item) in articles!!.withIndex()) {
                                        Log.i("artileList", item.urlToImage!!)

                                        coroutineScope {
                                            val dataImageFutureAsync = async {
                                                dataImageFuture = Glide.with(context)
                                                    .asBitmap()
                                                    //.into(500, 500).get()
                                                    .load(item.urlToImage).submit()
                                            }

                                            dataImageFutureAsync.await()
                                            if (dataImageFutureAsync.isCompleted) {
                                                if (dataImageFuture != null) {
                                                    val imageBitmapAsync = async {
                                                        imageBitmap = dataImageFuture!!.get()
                                                    }
                                                    imageBitmapAsync.await()
                                                    if (imageBitmapAsync.isCompleted) {
                                                        if (imageBitmap != null) {
                                                            ImageBase64 =
                                                                ImageBitmapString.BitMapToString(
                                                                    bitmap = imageBitmap!!
                                                                )
                                                        }

                                                        Log.i("ImageBase64", ImageBase64)
                                                        article =
                                                            Articles(
                                                                item.author,
                                                                item.title,
                                                                item.description,
                                                                item.url,
                                                                item.urlToImage,
                                                                item.publishedAt,
                                                                item.content,
                                                                null,
                                                                ImageBase64
                                                            )
                                                        CoroutineScope(Dispatchers.IO).launch {
                                                            articleDatabase.getArticleDao()
                                                                .insertArticle(
                                                                    article!!
                                                                )
                                                        }

                                                    }
                                                }
                                            }
                                        }


                                        // articleList.toMutableList().add(article)

                                        // if (!articleList.isNullOrEmpty()) {

                                        //}

                                        //articleList.add(article)
                                        // CoroutineScope(Dispatchers.IO).launch {
                                        //coroutineScope {
                                        //    val insertArticleAsync = async {

                                        /*GlobalScope.launch(Dispatchers.IO) {
                                            suspendCoroutine<Unit> {
                                                val dataImageAsync = async {
                                                    dataImageFuture = Glide.with(context)
                                                        .asBitmap()
                                                        //.into(500, 500).get()
                                                        .load(item.urlToImage).submit()
                                                }

                                                if (dataImageAsync.isCompleted) {
                                                    val imageStr = dataImageFuture?.get()

                                                    ImageBase64Async = async {
                                                        if (imageStr != null)
                                                        {
                                                            ImageBase64 =
                                                                ImageBitmapString.BitMapToString(bitmap = imageStr)
                                                            Log.i("ImageBase64", ImageBase64!!)
                                                        }
                                                    }
                                                    if(ImageBase64Async.isCompleted){

                                                    }
                                                }
                                            }
                                        }*/

                                        // Glide.with(context).clear(dataImageFuture);
                                        //val bitmap: Bitmap = dataImageFuture.get()


                                        // Log.i("ImageBase64", ImageBase64!!)
                                        //imageBitmap = dataResult.data as Bitmap


                                        //  }
                                        // insertArticleAsync.await()
                                        //  }

                                        //var insertAsync =  insertAsync.await()

                                        //}

                                        // dataResult.data = ImageBitmapString.ConvertUrlImageGetBitmap(context, item.urlToImage)
                                    }
                                    // insertDbArticleList(context, articles)

                                    /* val articleDatabase = ArticleDatabase.invoke(context)
                                     if (!articleList.isNullOrEmpty()) {
                                         CoroutineScope(Dispatchers.IO).launch {
                                                 //listOf<com.mindfulness.googlenewsembed.data.entities.news.Articles>(article)
                                             //if(article != null)
                                             articleDatabase.getArticleDao().insertArticle(articleList!!)
                                         }
                                     }*/

                                    emitter.onComplete()
                                } catch (ex: Exception) {
                                    //Log.i("Exception2", ex.localizedMessage!!)

                                    throwable = Throwable(ex.localizedMessage)
                                    emitter.onError(throwable)
                                }


                                // dataResult.data = articles as MutableList<Any>
                                //   return@withContext dataResult
                                // val complated = setArticleList2(context, articles!!)
                                /*   complated.doOnComplete {
                                       if (!this.isActive)
                                           emitter.onComplete()
                                   }*/
                                //     }
                            } else {
                                Log.i("articlesSize", "boş data1")
                                dataResult.message = "Boş data2"
                                //return@withContext dataResult
                            }
                            // emitter.onComplete()
                            //check
                            //Toast.makeText(this@MainActivity, "okk", Toast.LENGTH_SHORT).show()
                        } else {
                            throwable = Throwable(resultApi.errorBody().toString())
                            //emitter.onError(throwable)
                            dataResult.message = resultApi.errorBody().toString()
                        }
                    }
                } else {
                    //Toast.makeText(this@MainActivity,resultApi.,Toast.LENGTH_SHORT).show()
                    throwable = Throwable(resultApi.errorBody().toString())
                    //emitter.onError(throwable)
                    dataResult.message = resultApi.errorBody().toString()
                }
            }
        } catch (ex: Exception) {
            Log.i("Exception", ex.localizedMessage!!)
            dataResult.message = ex.localizedMessage
            //val throwable = Throwable(ex.localizedMessage)
            //emitter.onError(throwable)
        }
        // return dataResult
        //return Single<DataResult>()

    }
    */
}