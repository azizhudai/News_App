package com.mindfulness.googlenewsembed.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindfulness.googlenewsembed.adapters.ArticleAdapter
import com.mindfulness.googlenewsembed.data.entities.DataResult
import com.mindfulness.googlenewsembed.data.entities.news.Articles
import com.mindfulness.googlenewsembed.data.entities.response.ArticlesResponse
import com.mindfulness.googlenewsembed.databinding.ActivityMainBinding
import com.mindfulness.googlenewsembed.helper.isConnectivity
import com.mindfulness.googlenewsembed.interfaces.ICellClickListener
import com.mindfulness.googlenewsembed.interfaces.IResultListener
import com.mindfulness.googlenewsembed.room_database.ArticleDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.kodein.di.android.kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.mindfulness.googlenewsembed.R
import com.mindfulness.googlenewsembed.ui.web_view.NewsWebViewActivity


class MainActivity : AppCompatActivity(), IResultListener, KodeinAware,
    ICellClickListener<Articles> {

    override val kodein by kodein()
    private val factory: MainViewModelFactory by instance()
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    private lateinit var articleAdapter: ArticleAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProviders.of(this, factory)[MainViewModel::class.java]
        binding.viewmodel = mainViewModel
        mainViewModel.resultListener = this
        inits()
        val dataBaseInstance = ArticleDatabase.invoke(this)
        mainViewModel.setInstanceOfDb(dataBaseInstance)

        if (!isConnectivity(this)) {
            Toast.makeText(
                this,
                "İnternet Bağlantısı Yok! \n\nSon haberlere ulaşılamadı!",
                Toast.LENGTH_LONG
            ).show()
            observerViewModel()
            mainViewModel.getDBArticle()

        } else {
            observerArtcileResponse()
            Handler().postDelayed({
                mainViewModel.getDBArticle()
                observerViewModel()
            }, 3000)
        }

        // val data = mainViewModel.getDBArticle()
        //mainViewModel.clearArticles()
        //mainViewModel.getDBArticle()

        //observeConvertUrmImageToBitmapAllNews()
        /*  if (!data.isNullOrEmpty())
              Toast.makeText(this, data.value!!.size, Toast.LENGTH_SHORT).show()
          else
              Toast.makeText(this, "Boş Veri",Toast.LENGTH_SHORT).show()

  */
        //onStarted()

        //rv_news

        swipe.setOnRefreshListener {
            if (!isConnectivity(this)) {
                Toast.makeText(
                    this,
                    "İnternet Bağlantısı Yok! \n\nSon haberlere ulaşılamadı!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                observerArtcileResponse()
                Handler().postDelayed({
                    mainViewModel.getDBArticle()
                    observerViewModel()
                }, 3000)
            }
            Handler().postDelayed(Runnable {
                swipe.isRefreshing = false
            }, 3000)
        }
    }

    private fun observerViewModel() : MutableLiveData<List<Articles>>?{
        mainViewModel.personsList.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                articleAdapter.setArticlesList(it)
            } else {
                articleAdapter.setArticlesList(arrayListOf())
            }
        })
        return null
    }

    private fun inits() {
        articleAdapter = ArticleAdapter(this@MainActivity, this@MainActivity)
        recycler_view_news.apply {
            adapter = articleAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    private fun observeConvertUrmImageToBitmapAllNews(): MutableLiveData<DataResult<Articles>>? {
        /* mainViewModel.ConvertUrmImageToBitmapAllNews(this).observe(this, Observer {
             if (!it.isNullOrEmpty()){
                 Toast.makeText(this@MainActivity,it!!.size.toString()+it!!.get(0).title+ " Aziz", Toast.LENGTH_LONG).show()

             }
         })*/
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observerArtcileResponse() {//: MutableLiveData<List<ArticlesResponse>>?
        mainViewModel.GetNewsData().observe(this, Observer {
            try {
                if (!it.data.isNullOrEmpty()) {
                    mainViewModel.ConvertUrmImageToBitmapAllNews(this@MainActivity, it.data!!)
                        .observe(
                            this@MainActivity, Observer {
                              /*  if (!it.isNullOrEmpty()) {
                                    /*    Toast.makeText(
                                            this@MainActivity,
                                            it.size.toString() + it.get(0).ArticlesImage + " Aziz",
                                            Toast.LENGTH_LONG
                                        ).show()*/
                                } else {
                                    // checkConnectivity(this@MainActivity)
                                }*/
                            }
                        )
                }
            } catch (ex: Exception) {
                Toast.makeText(this@MainActivity, ex.message, Toast.LENGTH_SHORT).show()
            }
        })
       // return null
    }

    override fun onStarted() {
        //val newsAdapter = SimpleGenericRecyclerAdapter<List<Articles>>
        binding.progressbar.visibility = View.VISIBLE
    }

    override fun onSuccess() {
        //Toast.makeText(this, "Başarılı sonuç", Toast.LENGTH_SHORT).show()
        binding.progressbar.visibility = View.GONE
        // startHomeActivity()
    }

    override fun onFailure(message: String) {
        binding.progressbar.visibility = View.GONE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onCellClickListener(className: Articles) {

        if (!isConnectivity(this)) {
            Toast.makeText(
                this,
                "İnternet Bağlantısı Yok!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val intent = Intent(this@MainActivity, NewsWebViewActivity::class.java)
            if (!className.url.isNullOrEmpty()) {
                intent.putExtra("author",className.author)
                intent.putExtra("url", className.url)
                startActivity(intent)

            } else {
                Toast.makeText(this@MainActivity, "Url bağlantısı boş geldi!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

}