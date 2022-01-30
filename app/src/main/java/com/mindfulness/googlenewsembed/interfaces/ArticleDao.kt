package com.mindfulness.googlenewsembed.interfaces

import androidx.room.*
import com.mindfulness.googlenewsembed.data.entities.news.Articles
import io.reactivex.Single
import io.reactivex.annotations.SchedulerSupport

@SchedulerSupport(value = SchedulerSupport.IO)
@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: Articles)
    //@Ignore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg entity: Articles)

    @Update
    suspend fun updateArticle(article: Articles)

    @Delete
    suspend fun deleteArticle(article: Articles)

    //@Transaction
    @Query("SELECT * FROM Articles_db3 ORDER BY publishedAtDate DESC")
    fun getAllArticles(): Single<List<Articles>>

    @Query("SELECT * FROM Articles_db3 ORDER BY publishedAtDate DESC LIMIT 1")// ORDER BY publishedAt DESC")
    fun getLastArticle(): Articles

    @Query("DELETE FROM Articles_db3")
    suspend fun clearArticle()

    /*@Query("DELETE FROM Articles WHERE id = :id")
    suspend fun deleteArticleById(id: Int)*/

}