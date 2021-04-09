package com.example.tasktimer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class Task(var name: String, val sortOrder: Int) : Parcelable {
    var id: Long = 0
}