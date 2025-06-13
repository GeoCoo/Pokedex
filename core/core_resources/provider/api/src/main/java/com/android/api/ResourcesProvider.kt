package com.android.api

import android.content.Context
import androidx.annotation.StringRes

interface ResourceProvider {
    fun provideContext(): Context
    fun getString(@StringRes resId: Int): String
}