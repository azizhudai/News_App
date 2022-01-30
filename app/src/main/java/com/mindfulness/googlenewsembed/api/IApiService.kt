package com.mindfulness.googlenewsembed.api

import com.google.gson.JsonObject
import com.mindfulness.googlenewsembed.utils.ViewUtils
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface IApiService {

    @Headers("X-Api-Key: ${ViewUtils.API_KEY}")
    @GET("top-headlines")
    fun getNewsDataAsync(@Query("country") country:String):Deferred<Response<JsonObject>>

}