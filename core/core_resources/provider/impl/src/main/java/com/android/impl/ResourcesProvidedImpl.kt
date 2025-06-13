package com.android.impl

import android.content.Context
import androidx.annotation.StringRes
import com.android.api.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {

    override fun provideContext() = context
    override fun getString(@StringRes resId: Int): String = context.getString(resId)

}