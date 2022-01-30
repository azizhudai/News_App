package com.mindfulness.googlenewsembed.room_database

import android.content.Context
import androidx.room.*
import com.mindfulness.googlenewsembed.data.entities.news.Articles
import com.mindfulness.googlenewsembed.interfaces.ArticleDao
import java.util.*

@Database(entities = [Articles::class], version = DB_VERSION)
@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao

    companion object {
        private const val DB_NAME = "Article_db3.db"

        @Volatile
        private var instance: ArticleDatabase? = null
         private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticleDatabase::class.java,
            DB_NAME
        )
            //.addTypeConverter()
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}

private const val DB_VERSION = 5

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

/*
object DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}*/