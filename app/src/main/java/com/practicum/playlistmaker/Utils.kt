package com.practicum.playlistmaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    this.currentFocus?.let { view ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun Int.dxToPx(context: Context): Int {
    return context.resources.getDimensionPixelSize(this)
}

fun formatDuration(milliseconds: Long?): String {
    if (milliseconds == null) return "00:00"
    val secondsAmount = milliseconds / 1000
    val minutes = secondsAmount / 60
    val seconds = secondsAmount % 60
    return String.format("%02d:%02d", minutes, seconds)
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}