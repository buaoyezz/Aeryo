package net.zzbuaoye.aeryo

import android.app.Application
import net.zzbuaoye.aeryo.bookmarks.data.BookmarkDatabase

class AeryoApplication : Application() {
    val database: BookmarkDatabase by lazy {
        BookmarkDatabase.getDatabase(this)
    }
}
