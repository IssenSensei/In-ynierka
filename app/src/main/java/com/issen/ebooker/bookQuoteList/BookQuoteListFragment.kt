package com.issen.ebooker.bookQuoteList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.issen.ebooker.EBookerApplication
import com.issen.ebooker.R

class BookQuoteListFragment : Fragment() {

    private val viewModel: BookQuoteListViewModel by viewModels {
        BookQuoteListViewModelFactory(
            (requireActivity().application as EBookerApplication).booksRepository
        )
    }
    private val safeArgs: BookQuoteListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_quote_list, container, false)
        viewModel.bookId = safeArgs.bookId
        return view
    }
}