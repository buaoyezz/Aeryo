package net.zzbuaoye.aeryo.bookmarks.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val visitTime: Long = System.currentTimeMillis(),
    val favicon: ByteArray? = null
)
