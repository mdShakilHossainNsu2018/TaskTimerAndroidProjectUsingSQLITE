package com.example.tasktimer

import android.content.ContentUris
import android.net.Uri

object TasksContract {
    internal const val TABLE_NAME = "Tasks"

    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)

    const val CONTENT_TYPE = "vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"
    const val CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"

    object Columns {
        const val ID = "_id"
        const val TASK_NAME = "Name"
        const val TASK_Description = "Description"
        const val TASK_SORT_ORDER = "SortOrder"
    }

    fun getId(uri: Uri): Long{
        return ContentUris.parseId(uri)
    }

    fun buildUriFormId(id: Long): Uri {
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }
}