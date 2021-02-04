package com.issen.ebooker.userQuotes

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.issen.ebooker.R
import com.issen.ebooker.database.ApiInterface
import com.issen.ebooker.utils.Tools
import com.issen.ebooker.utils.ViewAnimation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserQuotesRecyclerViewAdapter(
    private var userQuotesMap: MutableList<UserQuotesItem>,
    private val context: Context
)
    : RecyclerView.Adapter<UserQuotesRecyclerViewAdapter.ViewHolder>() {

    override fun getItemCount() = userQuotesMap.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = userQuotesMap.toList()
        val second = data[position]

        holder.bookTitle.text = second.bookTitle
        holder.content.text = second.content

        holder.expand.setOnClickListener { v ->
            val show = toggleLayoutExpand(!second.expanded, v, holder.lytExpand)
            second.expanded = show
        }

        if (second.expanded) {
            holder.lytExpand.setVisibility(View.VISIBLE)
        } else {
            holder.lytExpand.setVisibility(View.GONE)
        }
        Tools.toggleArrow(second.expanded, holder.expand, false)

        holder.linearListener.setOnClickListener {
            seeDetails(context, second)
        }

        holder.delete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.delete_dialog_confirmation)
            builder.setPositiveButton(R.string.Confirm){
                    dialogInterface, i ->
                delete(second.id)
                userQuotesMap.removeAt(position)
                notifyItemRemoved(position)
            }
            builder.setNegativeButton(R.string.Dialog_cancel, null)
                .setOnCancelListener{
                        dialog ->  dialog.dismiss()
                }
            builder.show()
        }


    }

    private fun delete(quoteId: Int) {
        val apiInterface = ApiInterface.create().deleteQuote("deleteQuote", quoteId)

        apiInterface.enqueue(object : Callback<Int> {

            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if(response.isSuccessful) {
                    Log.i("addresponse", "post submitted to API." + response.body().toString())
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable?) {
                Log.d("Wystąpił błąd, spróbuj ponownie później", t.toString())

            }
        })
    }

    private fun seeDetails(context: Context, data: UserQuotesItem){

        val intent = Intent(context, UserQuoteDetailFragment::class.java)
        intent.apply {
            putExtra("data", data)
        }
        context.startActivity(intent)
    }

    private fun toggleLayoutExpand(show: Boolean, view: View, lyt_expand: View): Boolean {
        Tools.toggleArrow(show, view)
        if (show) {
            ViewAnimation.expand(lyt_expand)
        } else {
            ViewAnimation.collapse(lyt_expand)
        }
        return show
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_quotes_item, parent, false)
        return ViewHolder(view)
    }

    fun updateList(newUserQuotesMap: MutableList<UserQuotesItem>) {
            userQuotesMap = newUserQuotesMap
            notifyDataSetChanged()
    }


    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mItem: UserQuotesItem? = null

        val lytExpand = mView.findViewById(R.id.lyt_expand) as View
        val expand = mView.findViewById(R.id.bt_expand) as ImageButton

        val bookTitle = mView.findViewById<TextView>(R.id.bookTitle)!!
        val content = mView.findViewById<TextView>(R.id.quoteContent)!!
        val delete = mView.findViewById<ImageButton>(R.id.user_quote_delete)

        val linearListener = mView.findViewById<LinearLayout>(R.id.linear_listener)

    }
}