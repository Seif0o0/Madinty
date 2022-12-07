package com.madinaty.app.presentation.adapter

open class RetryClickListener(val clickListener: () -> Unit) {
    fun onRetry() = clickListener()
}