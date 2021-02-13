package com.issen.ebooker.bookLibrary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.issen.ebooker.data.BooksRepository
import com.issen.ebooker.data.domain.Book
import kotlinx.coroutines.launch

class BookLibraryViewModel(private val booksRepository: BooksRepository) : ViewModel() {
    private val favouriteShelfId = 0
    private val purchasedShelfId = 1
    private val toReadShelfId = 2
    private val readingShelfId = 3
    private val haveReadShelfId = 4
    private val reviewedShelfId = 5
    private val recentlyViewedShelfId = 6
    private val myEBooksShelfId = 7
    private val recommendationsShelfId = 8

    val readingList = MutableLiveData<List<Book>>()
    val toReadList = MutableLiveData<List<Book>>()
    val favouriteList = MutableLiveData<List<Book>>()
    val haveReadList = MutableLiveData<List<Book>>()
    val purchasedList = MutableLiveData<List<Book>>()
    val eBooksList = MutableLiveData<List<Book>>()
    val reviewedList = MutableLiveData<List<Book>>()
    val recommendationsList = MutableLiveData<List<Book>>()
    val recentlyViewedList = MutableLiveData<List<Book>>()

    init {
        viewModelScope.launch {
            readingList.value = booksRepository.getShelfBooks(readingShelfId)
            toReadList.value = booksRepository.getShelfBooks(toReadShelfId)
            recentlyViewedList.value = booksRepository.getShelfBooks(recentlyViewedShelfId)
            purchasedList.value = booksRepository.getShelfBooks(purchasedShelfId)
            recommendationsList.value = booksRepository.getShelfBooks(recommendationsShelfId)
            favouriteList.value = booksRepository.getShelfBooks(favouriteShelfId)
            haveReadList.value = booksRepository.getShelfBooks(haveReadShelfId)
            reviewedList.value = booksRepository.getShelfBooks(reviewedShelfId)
            eBooksList.value = booksRepository.getShelfBooks(myEBooksShelfId)
//            Log.e("shelves", booksRepository.getUserShelves().toString())
        }
    }
}