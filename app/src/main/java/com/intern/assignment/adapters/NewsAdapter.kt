package com.intern.assignment.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.intern.assignment.R
import com.intern.assignment.databinding.ItemArticleBinding
import com.intern.assignment.models.Article

class NewsAdapter(private val mList: List<Article>): RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(val binding : ItemArticleBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
       val article = mList[position]
        holder.binding.apply {
            Glide.with(root.context).load(article.urlToImage)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(imgArticle)
            txtSourceTime.text = article.source.name
            txtTitle.text = article.title
            txtDescription.text = article.description

            setOnItemClickListener {
                onItemClickListener?.let { it(article) }
            }
        }

    }

    override fun getItemCount(): Int {
        Log.e("ADAPTER",mList.size.toString())
        return mList.size
    }


    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit){
        onItemClickListener = listener
    }
}