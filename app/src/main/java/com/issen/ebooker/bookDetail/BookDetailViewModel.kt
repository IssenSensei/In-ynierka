package com.issen.ebooker.bookDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.issen.ebooker.EBookerApplication.Companion.favouriteShelfId
import com.issen.ebooker.EBookerApplication.Companion.haveReadShelfId
import com.issen.ebooker.EBookerApplication.Companion.toReadShelfId
import com.issen.ebooker.data.BooksRepository
import com.issen.ebooker.data.domain.Book
import com.issen.ebooker.data.domain.Review
import kotlinx.coroutines.launch

class BookDetailViewModel(private val booksRepository: BooksRepository) : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var databaseReviewRef = Firebase.database.getReference("reviews")

    lateinit var book: Book

    private val _rating = MutableLiveData<Float>()
    val rating: LiveData<Float>
        get() = _rating

    private val _addReview = MutableLiveData<Boolean>()
    val addReview: LiveData<Boolean>
        get() = _addReview

    private lateinit var _isFavourite: LiveData<Boolean>
    val isFavourite: LiveData<Boolean>
        get() = _isFavourite

    private lateinit var _haveRead: LiveData<Boolean>
    val haveRead: LiveData<Boolean>
        get() = _haveRead

    private lateinit var _isToRead: LiveData<Boolean>
    val isToRead: LiveData<Boolean>
        get() = _isToRead

    private val _reviewedByUser = MutableLiveData<Boolean>()
    val reviewedByUser: LiveData<Boolean>
        get() = _reviewedByUser

    fun toggleFavourite() {
        viewModelScope.launch {
            if (_isFavourite.value!!) {
                booksRepository.removeFromShelf(book.bookId, favouriteShelfId, auth.currentUser!!.uid)
            } else {
                booksRepository.addToShelf(book.bookId, favouriteShelfId, auth.currentUser!!.uid)
            }
        }
    }

    fun toggleToRead() {
        viewModelScope.launch {
            if (_isToRead.value!!) {
                booksRepository.removeFromShelf(book.bookId, toReadShelfId, auth.currentUser!!.uid)
            } else {
                booksRepository.addToShelf(book.bookId, toReadShelfId, auth.currentUser!!.uid)
            }
        }
    }

    fun toggleHaveRead() {
        viewModelScope.launch {
            if (_haveRead.value!!) {
                booksRepository.removeFromShelf(book.bookId, haveReadShelfId, auth.currentUser!!.uid)
            } else {
                booksRepository.addToShelf(book.bookId, haveReadShelfId, auth.currentUser!!.uid)
            }
        }
    }

    fun saveReview(content: String) {
        viewModelScope.launch {
            val reviewItem = Review(
                auth.currentUser!!.uid,
                auth.currentUser!!.displayName ?: "Anonimowy",
                book.title,
                content,
                _rating.value!!
            )
            databaseReviewRef.child(book.bookId).child(auth.currentUser!!.uid).setValue(reviewItem)
            _reviewedByUser.value = true
        }
    }

    fun setAddReview(value: Boolean) {
        _addReview.value = value
    }

    fun setSelectedBook(selectedBook: Book) {
        book = selectedBook
        _isFavourite = booksRepository.checkIsOnShelf(book.bookId, favouriteShelfId, auth.currentUser!!.uid)
        _isToRead = booksRepository.checkIsOnShelf(book.bookId, toReadShelfId, auth.currentUser!!.uid)
        _haveRead = booksRepository.checkIsOnShelf(book.bookId, haveReadShelfId, auth.currentUser!!.uid)
        _addReview.value = false
        viewModelScope.launch {
            databaseReviewRef.child(book.bookId).child(auth.currentUser!!.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _reviewedByUser.value = snapshot.exists()
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    fun setRating(rating: Float) {
        _rating.value = rating
    }

}