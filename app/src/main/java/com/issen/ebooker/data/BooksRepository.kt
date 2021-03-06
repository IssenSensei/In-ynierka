package com.issen.ebooker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.map
import com.issen.ebooker.data.conversionExtensions.*
import com.issen.ebooker.data.domain.Book
import com.issen.ebooker.data.local.dao.*
import com.issen.ebooker.data.remote.GoogleApiNetwork.googleApi
import com.issen.ebooker.data.remote.models.ResponseBookshelfList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.issen.ebooker.data.local.models.DatabaseUserBookItem

class BooksRepository(
    private val bookDao: BookDao,
    private val imageLinksDao: ImageLinksDao,
    private val userBookDao: UserBookDao
) {

    val books: LiveData<List<Book>> = Transformations.map(bookDao.getBooks()) {
        it.asDomainModel()
    }

    fun getShelfBooks(id: Int, uid: String): LiveData<List<Book>> {
        return bookDao.getShelfBooks(id, uid).map {
            it.asDomainModel()
        }
    }

    suspend fun getUserShelves(): ResponseBookshelfList {
        return googleApi.getUserShelves()
    }

    fun getAuthorBooks(author: String): LiveData<List<Book>> {
        return bookDao.getAuthorBooks(author).map {
            it.asDomainModel()
        }
    }

    fun getPublisherBooks(publisher: String): LiveData<List<Book>> {
        return bookDao.getPublisherBooks(publisher).map {
            it.asDomainModel()
        }
    }

    fun getQueriedBooks(filter: String): LiveData<List<Book>> {
        return bookDao.getQueriedBooks(filter).map {
            it.asDomainModel()
        }
    }

    suspend fun refreshBooks() {
        withContext(Dispatchers.IO) {
            googleApi.getBooks().items?.forEach {
                val imageLinksId = if (it.volumeInfo.imageLinks != null) {
                    imageLinksDao.insert(it.volumeInfo.imageLinks.asDatabaseImageLinks())
                } else null
                bookDao.insert(it.asDatabaseBookItem(imageLinksId?.toInt()))
            }
        }
    }

    suspend fun refreshShelves(uid: String) {
        withContext(Dispatchers.IO) {
            googleApi.getUserShelves().items?.forEach { bookshelf ->
                if (bookshelf.id < 9) {
                    val bookList = mutableListOf<DatabaseUserBookItem>()
                    googleApi.getShelfBooks(bookshelf.id).items?.forEach {
                        bookList.add(DatabaseUserBookItem(it.id, bookshelf.id, uid))
                        val imageLinksId = if (it.volumeInfo.imageLinks != null) {
                            imageLinksDao.insert(it.volumeInfo.imageLinks.asDatabaseImageLinks())
                        } else null
                        bookDao.insert(it.asDatabaseBookItem(imageLinksId?.toInt()))
                    }
                    userBookDao.refreshShelfBooks(bookList)
                }
            }
        }
    }

    suspend fun refreshAuthorBooks(author: String) {
        googleApi.getQueriedBooks("inauthor:$author").items?.forEach {
            val imageLinksId = if (it.volumeInfo.imageLinks != null) {
                imageLinksDao.insert(it.volumeInfo.imageLinks.asDatabaseImageLinks())
            } else null
            bookDao.insert(it.asDatabaseBookItem(imageLinksId?.toInt()))
        }
    }

    suspend fun refreshPublisherBooks(publisher: String) {
        withContext(Dispatchers.IO) {
            googleApi.getQueriedBooks("inpublisher$publisher").items?.forEach {
                val imageLinksId = if (it.volumeInfo.imageLinks != null) {
                    imageLinksDao.insert(it.volumeInfo.imageLinks.asDatabaseImageLinks())
                } else null
                bookDao.insert(it.asDatabaseBookItem(imageLinksId?.toInt()))
            }
        }
    }

    suspend fun removeFromShelf(bookId: String, shelfId: Int, uid: String) {
        userBookDao.removeFromShelf(DatabaseUserBookItem(bookId, shelfId, uid))
        googleApi.removeFromUserShelf(shelfId, bookId)
    }

    fun checkIsOnShelf(bookId: String, shelfId: Int, uid: String): LiveData<Boolean> {
        return userBookDao.checkIsOnShelf(bookId, shelfId, uid)
    }

    suspend fun addToShelf(bookId: String, shelfId: Int, uid: String) {
        userBookDao.addToShelf(DatabaseUserBookItem(bookId, shelfId, uid))
        googleApi.addToUserShelf(shelfId, bookId)
    }

    suspend fun getBookTitle(bookId: String): String {
        return bookDao.getBookTitle(bookId) ?: ""
    }

    suspend fun refreshFilteredBooks(query: String) {
        googleApi.getQueriedBooks(query).items?.forEach {
            val imageLinksId = if (it.volumeInfo.imageLinks != null) {
                imageLinksDao.insert(it.volumeInfo.imageLinks.asDatabaseImageLinks())
            } else null
            bookDao.insert(it.asDatabaseBookItem(imageLinksId?.toInt()))
        }
    }
}