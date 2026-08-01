package net.zzbuaoye.aeryo.bookmarks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val folder: String = "默认书签",
    val addedTime: Long = System.currentTimeMillis()
)
