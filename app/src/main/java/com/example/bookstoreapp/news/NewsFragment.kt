package com.example.bookstoreapp.news

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookstoreapp.R
import com.example.bookstoreapp.database.ApiInterface
import com.example.bookstoreapp.utils.LineItemDecoration
import com.example.bookstoreapp.utils.getNewsAdapter
import kotlinx.android.synthetic.main.layout_news_list.*
import kotlinx.android.synthetic.main.layout_user_quotes_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment: Fragment() {

    private lateinit var newsMap: MutableList<NewsItem>
    private lateinit var newsRecycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.layout_news_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newsRecycler = view!!.findViewById(R.id.news_recycler_view)
        setUpRecycler()
        getData()

        news_fab.setOnClickListener {
            showCustomDialog()
        }

        news_swipe_layout.setOnRefreshListener {
            getData()
            news_swipe_layout.isRefreshing = false
        }
    }


    private fun setUpRecycler() {
        newsRecycler.layoutManager = LinearLayoutManager(context)
        newsRecycler.addItemDecoration(
            LineItemDecoration(
                this.context,
                LinearLayout.VERTICAL
            )
        )
        newsRecycler.adapter = NewsRecyclerViewAdapter(mutableListOf(), this.context!!)
    }

    private fun getData() {
        val apiInterface = ApiInterface.create().getNews("getNews")

        apiInterface.enqueue( object : Callback<List<NewsItem>> {

            override fun onResponse(call: Call<List<NewsItem>>, response: Response<List<NewsItem>>?) {
                if(response?.body() != null) {
                    newsMap = response.body() as MutableList<NewsItem>
                    if (newsMap.size == 0) {
                        no_news_data_text_view.visibility = View.VISIBLE
                    }
                    else {
                        newsRecycler.getNewsAdapter().updateList(newsMap)
                    }
                }
            }

            override fun onFailure(call: Call<List<NewsItem>>?, t: Throwable?) {
                Toast.makeText(context, "Wystąpił problem przy pobieraniu danych nw", Toast.LENGTH_SHORT).show()
                Log.d("qqqqqq", t.toString())

            }
        })
    }

    private fun showCustomDialog() {

        val dialog = Dialog(this.context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_news_search)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        (dialog.findViewById(R.id.news_search_button_cancel) as Button).setOnClickListener { dialog.dismiss() }
        (dialog.findViewById(R.id.news_search_button_save) as Button).setOnClickListener {
            val title = dialog.findViewById<EditText>(R.id.news_search_title)
            val description = dialog.findViewById<EditText>(R.id.news_search_content)
            val author = dialog.findViewById<EditText>(R.id.news_search_author)
            var list = mutableListOf<NewsItem>()
            list.addAll(newsMap)
            newsMap.clear()
            if (!title.text.toString().equals("")) {
                list = list.filter {
                    it.title.toLowerCase().contains(title.text.toString().toLowerCase())
                } as MutableList<NewsItem>
            }
            if (!description.text.toString().equals("")) {
                list = list.filter {
                    it.content.toLowerCase().contains(description.text.toString().toLowerCase())
                } as MutableList<NewsItem>
            }
//            if (!author.text.toString().equals("")) {
//                list = list.filter {
//                    it.login.toLowerCase().contains(author.text.toString().toLowerCase())
//                } as MutableList<NewsItem>
//            }

            newsMap.addAll(list)

            newsRecycler.getNewsAdapter().notifyDataSetChanged()
            dialog.dismiss()
        }


        dialog.show()
        dialog.window!!.attributes = lp
    }
}