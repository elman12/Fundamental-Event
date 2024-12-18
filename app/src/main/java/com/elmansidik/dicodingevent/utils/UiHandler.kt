package com.elmansidik.dicodingevent.utils

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

object UiHandler {

    fun showLoading(isLoading: Boolean, progressBar: ProgressBar?) {
        if (progressBar != null) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    fun handleError(
        isError: Boolean,
        message: String?,
        errorTextView: TextView?,
        refreshButton: MaterialButton?,
        recyclerView: RecyclerView?,
        onRetry: () -> Unit
    ) {
        if (isError) {
            if (errorTextView != null) {
                errorTextView.visibility = View.VISIBLE
            }
            if (errorTextView != null) {
                errorTextView.text = message ?: "Terjadi kesalahan"
            }
            if (refreshButton != null) {
                refreshButton.visibility = View.VISIBLE
            }
            if (refreshButton != null) {
                refreshButton.setOnClickListener {
                    onRetry()
                }
            }
            if (recyclerView != null) {
                recyclerView.visibility = View.VISIBLE
            }
        } else {
            if (errorTextView != null) {
                errorTextView.visibility = View.GONE
            }
            if (refreshButton != null) {
                refreshButton.visibility = View.GONE
            }
        }
    }
}