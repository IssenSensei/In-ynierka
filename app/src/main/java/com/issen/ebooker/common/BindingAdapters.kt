package com.issen.ebooker.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.issen.ebooker.R

@BindingAdapter("thumbnail")
fun setThumbnail(view: ImageView, url: String?) {
    Glide.with(view.context).load(url).placeholder(R.drawable.ic_more).into(view)
}

@BindingAdapter("roundedRatingText")
fun setRoundedRatingText(view: TextView, rating: Float) {
    view.text = view.context.resources.getString(R.string.rating_text, "%.2f".format(rating))
}

@BindingAdapter("listSize", "isObserving")
fun setNoDataVisibility(view: TextView, listSize: Int, isObserving: Boolean) {
    if (isObserving) {
        if (listSize == 0) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    } else {
        view.visibility = View.GONE
    }
}