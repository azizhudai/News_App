package com.mindfulness.googlenewsembed.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.ZonedDateTime
import java.util.*

class Helper{
    companion object DateConverter {

        @RequiresApi(Build.VERSION_CODES.O)
        fun ZonedDateTimeToDate(strZonedDateTime: String):Date?{
            val zonedDateTime = ZonedDateTime.parse(strZonedDateTime)
            return Date.from(zonedDateTime.toInstant())
        }
    }
}

