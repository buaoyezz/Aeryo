package net.zzbuaoye.aeryo.browser.model

/**
 * Model representing a detected target from a long-press on a web element.
 */
sealed class ContextMenuTarget {
    /**
     * Long-pressed on an image (optionally wrapped in an anchor link).
     */
    data class Image(
        val imageUrl: String,
        val linkUrl: String? = null,
        val title: String = ""
    ) : ContextMenuTarget()

    /**
     * Long-pressed on a hyperlink.
     */
    data class Link(
        val url: String,
        val text: String = ""
    ) : ContextMenuTarget()

    /**
     * Long-pressed on an audio/video media element.
     */
    data class Media(
        val mediaUrl: String,
        val title: String = ""
    ) : ContextMenuTarget()

    /**
     * Long-pressed on a phone number, email address, or geo link.
     */
    data class Action(
        val actionUrl: String,
        val actionType: ActionType,
        val label: String = ""
    ) : ContextMenuTarget()

    enum class ActionType {
        PHONE, EMAIL, GEO
    }
}
