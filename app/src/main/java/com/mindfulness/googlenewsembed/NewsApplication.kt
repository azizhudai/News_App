package com.mindfulness.googlenewsembed

import android.app.Application
import com.mindfulness.googlenewsembed.repositories.ArticleRepository
import com.mindfulness.googlenewsembed.repositories.ArticleSource
import com.mindfulness.googlenewsembed.ui.main.MainViewModel
import com.mindfulness.googlenewsembed.ui.main.MainViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class NewsApplication:Application(),KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@NewsApplication))

        bind() from singleton { ArticleSource() }
        bind() from singleton { ArticleRepository(instance(),instance()) }

        bind() from provider { MainViewModel(instance(),instance()) }
        bind() from provider { MainViewModelFactory(instance(),instance()) }

    }
}