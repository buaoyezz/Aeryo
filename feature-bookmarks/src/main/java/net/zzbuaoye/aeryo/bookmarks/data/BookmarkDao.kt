package net.zzbuaoye.aeryo.bookmarks.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE kind = 'bookmark' ORDER BY addedTime DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE kind = 'favorite' ORDER BY addedTime DESC")
    fun getAllFavorites(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url AND kind = 'favorite')")
    fun isBookmarked(url: String): Flow<Boolean>

    // 历史记录操作
    @Query("SELECT * FROM history ORDER BY visitTime DESC LIMIT 200")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("UPDATE history SET favicon = :favicon WHERE url = :url")
    suspend fun updateHistoryFaviconByUrl(url: String, favicon: ByteArray)

    // 隐私历史记录操作
    @Query("SELECT * FROM private_history ORDER BY visitTime DESC LIMIT 200")
    fun getAllPrivateHistory(): Flow<List<PrivateHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateHistory(history: PrivateHistoryEntity)

    @Query("DELETE FROM private_history")
    suspend fun clearPrivateHistory()

    @Delete
    suspend fun deletePrivateHistory(history: PrivateHistoryEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM private_history WHERE url = :url)")
    fun isSavedInPrivateHistory(url: String): Flow<Boolean>
}
