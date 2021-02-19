package com.issen.ebooker.bookReviewsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.issen.ebooker.EBookerApplication
import com.issen.ebooker.data.local.models.DatabaseReviewItem
import com.issen.ebooker.databinding.FragmentBookReviewsListBinding
import kotlinx.android.synthetic.main.activity_main.*


class BookReviewsListFragment : Fragment() {

    private val safeArgs: BookReviewsListFragmentArgs by navArgs()
    private val viewModel: BookReviewsListViewModel by viewModels {
        BookReviewsListViewModelFactory(
            (requireActivity().application as EBookerApplication).booksRepository,
            safeArgs.bookId
        )
    }
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: BookReviewsFirebaseRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBookReviewsListBinding.inflate(inflater, container, false)
        viewModel.bookTitle.observe(viewLifecycleOwner, Observer {
            requireActivity().toolbar.title = it
        })

        val options: FirebaseRecyclerOptions<DatabaseReviewItem> = FirebaseRecyclerOptions.Builder<DatabaseReviewItem>()
            .setQuery(viewModel.databaseRef, DatabaseReviewItem::class.java)
            .build()

        adapter = BookReviewsFirebaseRecyclerAdapter(options)
        adapter.registerAdapterDataObserver(
            BookReviewsFirebaseRecyclerObserver(
                binding.bookReviewsListRecyclerView,
                adapter,
                LinearLayoutManager(requireContext())
            )
        )
        binding.bookReviewsListRecyclerView.adapter = adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}