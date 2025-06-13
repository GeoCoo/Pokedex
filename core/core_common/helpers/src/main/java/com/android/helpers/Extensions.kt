package com.android.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import android.util.Base64
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

fun <T, K> List<T>?.buildCategoryList(
    allCategoryLabel: String,
    allCategoryId: String,
    categorySelector: (T) -> String?,
    categoryMapper: (label: String, id: String) -> K
): List<K> = buildList {
    if (this@buildCategoryList.isNullOrEmpty()) return@buildList
    add(categoryMapper(allCategoryLabel, allCategoryId))
    this@buildCategoryList
        .mapNotNull(categorySelector)
        .distinct()
        .forEach { category ->
            add(categoryMapper(category.replaceFirstChar { it.uppercase() }, category))
        }
}

fun <T> Flow<T>.safeAsync(with: (Throwable) -> (T)): Flow<T> {
    return this.flowOn(Dispatchers.IO).catch { emit(with(it)) }
}

fun Context.isLocationPermissionGranted(): Boolean {
    val coarseLocationPermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val fineLocationPermission = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    return coarseLocationPermission == PackageManager.PERMISSION_GRANTED && fineLocationPermission == PackageManager.PERMISSION_GRANTED
}

fun Context.isLocationServiceEnabled(): Boolean {
    val lm = this
        .getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(lm)
}

fun String.linkify(
    linkStyle: SpanStyle,
) = buildAnnotatedString {
    append(this@linkify)
    val spannable = SpannableString(this@linkify)
    Linkify.addLinks(
        spannable,
        Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS
    )
    val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
    for (span in spans) {
        val start = spannable.getSpanStart(span)
        val end = spannable.getSpanEnd(span)

        addStyle(
            start = start,
            end = end,
            style = linkStyle,
        )
        addStringAnnotation(
            tag = "URL",
            annotation = span.url,
            start = start,
            end = end
        )
    }
}

fun String?.decodeBase64(): String {
    if (this == null) return ""
    val byte = Base64.decode(this, Base64.NO_WRAP)
    return String(byte, charset("UTF-8"))
}

fun String.encodeBase64(): String {
    val input = this.toByteArray(charset("UTF-8"))
    return Base64.encodeToString(input, Base64.NO_WRAP)
}

fun String.toCommaSeparatedList(): List<String> =
    this.split(",").map { it.trim() }

fun ClosedFloatingPointRange<Float>.lerp(fraction: Float): Float {
    return start + (endInclusive - start) * fraction
}