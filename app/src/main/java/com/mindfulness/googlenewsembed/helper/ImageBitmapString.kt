package com.mindfulness.googlenewsembed.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.room.TypeConverter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mindfulness.googlenewsembed.data.entities.DataResult
import com.mindfulness.googlenewsembed.data.entities.response.ArticlesResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.lang.Exception

object ImageBitmapString {

    var dataResult = DataResult<ArticlesResponse>("", null)

    @TypeConverter
     fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT) ?: ""//null
    }

    @TypeConverter
    suspend fun StringToBitMap(encodedString: String?): Bitmap? {
        return try {
            val encodeByte: ByteArray = Base64.decode(encodedString, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            bitmap
        } catch (e: Exception) {
            e.message
            null
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun ImageUrlToBitmap(context: Context, path: String): Bitmap =
        suspendCancellableCoroutine { continuation ->
            Glide
                .with(context)
                .asBitmap()
                .timeout(60000)
                .load(path)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, x: Transition<in Bitmap>?) {
                        continuation.resume(resource) {
                            resource
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                })
        }

    fun ConvertUrlImageGetBitmap(contex: Context, url: String?): DataResult<ArticlesResponse> {

        try {
            Glide.with(contex)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        // var result =  ImageBitmapString.BitMapToString(resource)
                        //var aaa = ImageBitmapString.StringToBitMap(result)
                        //Log.i("result",result!!)
                      //  dataResult = DataResult("", resource)
                        //dataResult.data = resource

                        //        imgView.setImageBitmap(aaa)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // this is called when imageView is cleared on lifecycle call or for
                        // some other reason.
                        // if you are referencing the bitmap somewhere else too other than this imageView
                        // clear it here as you can no longer have the bitmap
                    }
                })
        } catch (ex: Exception) {
            dataResult = DataResult(ex.localizedMessage, null)
            //dataResult.message = ex.localizedMessage
        }

        return dataResult
    }
}