package com.android.feature_tests

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

const val CONFIG_SDK = 33

@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [CONFIG_SDK],
    instrumentedPackages = [
        "androidx.loader.content"
    ])
abstract class RobolectricTest