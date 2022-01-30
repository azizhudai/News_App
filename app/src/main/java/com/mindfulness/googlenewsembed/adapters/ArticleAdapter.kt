package com.mindfulness.googlenewsembed.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mindfulness.googlenewsembed.data.entities.news.Articles
import com.mindfulness.googlenewsembed.databinding.ActivityMainBinding
import com.mindfulness.googlenewsembed.databinding.ItemNewsBinding
import com.mindfulness.googlenewsembed.interfaces.ICellClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArticleAdapter(
    private val context: Context,
    private val cellClickListener: ICellClickListener<Articles>
) :
    RecyclerView.Adapter<ArticleViewHolder>() {

    var articles = mutableListOf<Articles>()

    @SuppressLint("NotifyDataSetChanged")
    fun setArticlesList(components: List<Articles>) {
        //clear()
        this.articles = components.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewsBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        holder.binding.textViewTitle.text = articles[position].title
        holder.binding.textViewDescription.text = articles[position].description
        holder.binding.textViewAuthor.text = articles[position].author

        if (!articles[position].urlToImage.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val imageBitmap: String =
                    setImageData(articles[position].urlToImage, holder.binding.imageView)
            }

            //holder.binding.imageView.setImageBitmap(imageBitmap)
        }

        holder.itemView.setOnClickListener {
            cellClickListener.onCellClickListener(articles[position])
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun setImageData(url: String?, imageView: ImageView): String {

        var resource = ""
        try {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(
                    RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        imageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        } catch (ex: Exception) {
            resource = ex.message.toString()
        }
        return resource
    }
}

class ArticleViewHolder(val binding: ItemNewsBinding) :
    RecyclerView.ViewHolder(binding.root)