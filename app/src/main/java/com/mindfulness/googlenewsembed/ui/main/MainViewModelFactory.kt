package com.mindfulness.googlenewsembed.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mindfulness.googlenewsembed.repositories.ArticleRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val context: Context,
    private val repository: ArticleRepository):
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(context,repository) as T
    }
}