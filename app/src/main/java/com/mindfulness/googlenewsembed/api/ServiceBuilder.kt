package com.mindfulness.googlenewsembed.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    var BASE_URL = "https://newsapi.org/v2/"
    private val client = OkHttpClient.Builder().build()
    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    val retrofitService: IApiService by lazy {
        retrofit().create(IApiService::class.java)
    }
}