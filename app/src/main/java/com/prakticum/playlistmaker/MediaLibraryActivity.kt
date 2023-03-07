package com.prakticum.playlistmaker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class MediaLibraryActivity : AppCompatActivity() {
    companion object{
        val hamsterNames = listOf("Алиса", "Бимбо","Вжик","Дасти", "Китти", "Мафин")

        val hamsterContent = listOf("Говоря об отличии сирийских хомяков от обычных джунгариков, Марина Олеговна отмечает, что особой разницы, кроме размера, нет",
            "Помимо сбалансированного корма, который можно купить в зоомагазине, в рацион следует включать свежую траву, сено, овощи, фрукты.",
            "Регулярно следует чистить вольер, менять подстилку.",
            "Хомяки довольно активные животные. Для того чтобы животным было комфортно, в клетке, вольере следует установить различные приспособления.",
            "Оптимальная для содержания хомяков температура воздуха — 20–24С.",
            "Выпускать хомяков из клетки побегать по комнате можно, но при условии, что животное ручное.")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recycler.adapter = NewsAdapter(
            news = List(100) {
                News(it,getRandomName(),getRandomContent())
            },
            onItemClicked = {

            }
        )

    }
    // other code in activity
    private fun getRandomName(): String = hamsterNames[(0..5).random()]

    private fun getRandomContent(): String = hamsterContent[(0..5).random()]
// other code in activity

}
class NewsViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {

    private val sourceName: TextView
    private val text: TextView

    init {
      //  val itemView = LayoutInflater.from(parentView.context).inflate(R.layout.activity_media_library, parentView, false)
        sourceName = parentView.findViewById(R.id.sourceName)
        text = parentView.findViewById(R.id.text)
    }
    fun bind(model: News) {
        // присваиваем в TextView значения из нашей модели
        sourceName.text = model.name
        text.text = model.content
    }
}
data class News(val id: Int, val name: String, val content: String)

class NewsAdapter(
    private val news: List<News>,
    onItemClicked: () -> Unit
) : RecyclerView.Adapter<NewsViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_view, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(news[position])
    }

    override fun getItemCount() = news.size
}