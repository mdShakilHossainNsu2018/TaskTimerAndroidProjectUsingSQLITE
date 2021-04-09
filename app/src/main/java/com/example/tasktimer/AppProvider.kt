package com.example.tasktimer

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import java.lang.IllegalArgumentException

const val CONTENT_AUTHORITY = "com.example.tasktimer.provider"

private const val   TASKS = 100
private const val TASKS_ID = 101

private const val TIMINGS = 200
private const val TIMING_ID = 201

private const val TASKS_DURATIONS = 400
private const val TASKS_DURATIONS_ID = 401

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

class AppProvider: ContentProvider() {

    private val uriMatcher by lazy {buildUriMatcher()}

    private fun buildUriMatcher(): UriMatcher{
        val matcher = UriMatcher(UriMatcher.NO_MATCH)

        matcher.addURI(CONTENT_AUTHORITY, TasksContract.TABLE_NAME, TASKS)
        matcher.addURI(CONTENT_AUTHORITY, "${TasksContract.TABLE_NAME}/#", TASKS_ID)


        matcher.addURI(CONTENT_AUTHORITY, TimingsContract.TABLE_NAME, TIMINGS)
        matcher.addURI(CONTENT_AUTHORITY, "${TimingsContract.TABLE_NAME}/#", TIMING_ID)

//
//        matcher.addURI(CONTENT_AUTHORITY, DurationsContract.TABLE_NAME, TASKS_DURATIONS)
//        matcher.addURI(CONTENT_AUTHORITY, "${DurationsContract.TABLE_NAME}/#", TASKS_DURATIONS_ID)


        return matcher
    }

    override fun onCreate(): Boolean {

        return true

    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val match = uriMatcher.match(uri)

        val queryBuilder = SQLiteQueryBuilder()

        when(match){
            TASKS -> queryBuilder.tables = TasksContract.TABLE_NAME

            TASKS_ID -> {
                queryBuilder.tables = TasksContract.TABLE_NAME
                val tasksId = TasksContract.getId(uri)
                queryBuilder.appendWhere("${TasksContract.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$tasksId")
            }


            TIMINGS -> queryBuilder.tables = TimingsContract.TABLE_NAME

            TIMING_ID -> {
                queryBuilder.tables = TimingsContract.TABLE_NAME
                val timingId = TimingsContract.getId(uri)
                queryBuilder.appendWhere("${TimingsContract.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$timingId")
            }
//
//            TASKS_DURATIONS -> queryBuilder.tables = DurationsContract.TABLE_NAME
//
//            TASKS_DURATIONS_ID -> {
//                queryBuilder.tables = DurationsContract.TABLE_NAME
//                val durationId = DurationsContract.getId(uri)
//                queryBuilder.appendWhere("${DurationsContract.Columns.ID} = ")
//                queryBuilder.appendWhereEscapeString("$durationId")
//            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val db = context?.let { AppDatabase.getInstance(it).readableDatabase }
        val cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)

        return cursor
    }

    override fun getType(uri: Uri): String? {
        val match = uriMatcher.match(uri)

        return when(match){
            TASKS -> TasksContract.CONTENT_TYPE
            TASKS_ID -> TasksContract.CONTENT_ITEM_TYPE

            TIMINGS -> TimingsContract.CONTENT_TYPE
            TIMING_ID -> TimingsContract.CONTENT_ITEM_TYPE
//
//            TASKS_DURATIONS -> DurationsContract.CONTENT_TYPE
//            TASKS_DURATIONS_ID -> DurationsContract.CONTENT_ITEM_TYPE


            else -> throw IllegalArgumentException("unknown uri $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val match = uriMatcher.match(uri)

        val recordId: Long
        val returnUri: Uri

        when(match){
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase

                recordId = db.insert(TasksContract.TABLE_NAME, null, values)


                if (recordId != 1L){
                    returnUri = TasksContract.buildUriFormId(recordId)
                } else{
                    throw SQLException("Failed to insert, uri was $uri")

                }
            }

            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase

                recordId = db.insert(TimingsContract.TABLE_NAME, null, values)


                if (recordId != 1L){
                    returnUri = TimingsContract.buildUriFormId(recordId)
                } else{
                    throw SQLException("Failed to insert, uri was $uri")

                }
            }

            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }

        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val match = uriMatcher.match(uri)

        val count: Int
        var selectionCriteria: String

        when(match){
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(TasksContract.TABLE_NAME, selection, selectionArgs)
            }

            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"

                if (selectionCriteria != null && selection?.isNotEmpty()!!){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TasksContract.TABLE_NAME, selectionCriteria, selectionArgs)
            }


            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(TimingsContract.TABLE_NAME, selection, selectionArgs)
            }

            TIMING_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"

                if (selectionCriteria != null && selection?.isNotEmpty()!!){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.delete(TimingsContract.TABLE_NAME, selectionCriteria, selectionArgs)
            }

            else -> throw IllegalArgumentException("Unknown Uri: $uri")
        }
        return count
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val match = uriMatcher.match(uri)

        val count: Int
        var selectionCriteria: String

        when(match){
            TASKS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.update(TasksContract.TABLE_NAME, values, selection, selectionArgs)
            }

            TASKS_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TasksContract.getId(uri)
                selectionCriteria = "${TasksContract.Columns.ID} = $id"

                if (selectionCriteria != null && selection?.isNotEmpty() == true){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TasksContract.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }


            TIMINGS -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.update(TimingsContract.TABLE_NAME, values, selection, selectionArgs)
            }

            TIMING_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = TimingsContract.getId(uri)
                selectionCriteria = "${TimingsContract.Columns.ID} = $id"

                if (selectionCriteria != null && selection?.isNotEmpty()!!){
                    selectionCriteria += " AND ($selection)"
                }
                count = db.update(TimingsContract.TABLE_NAME, values, selectionCriteria, selectionArgs)
            }

            else -> throw IllegalArgumentException("Unknown Uri: $uri")
        }
        return count
    }
}