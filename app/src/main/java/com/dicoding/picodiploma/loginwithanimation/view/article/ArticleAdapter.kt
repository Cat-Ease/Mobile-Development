package com.dicoding.picodiploma.loginwithanimation.view.article

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.squareup.picasso.Picasso

class ArticleAdapter(private val articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.article_title)
        val description: TextView = itemView.findViewById(R.id.article_description)
        val image: ImageView = itemView.findViewById(R.id.article_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.title.text = article.title
        holder.description.text = article.description
        Picasso.get().load(article.image).into(holder.image) // Menggunakan Picasso untuk memuat gambar

        // Set click listener untuk membuka detail artikel
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ArticleDetailActivity::class.java).apply {
                putExtra("ARTICLE_ID", article.id)
                putExtra("ARTICLE_TITLE", article.title)
                putExtra("ARTICLE_DESCRIPTION", article.description)
                putExtra("ARTICLE_IMAGE", article.image)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }
}