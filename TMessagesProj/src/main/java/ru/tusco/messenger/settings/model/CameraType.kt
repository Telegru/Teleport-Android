package ru.tusco.messenger.settings.model

import androidx.annotation.StringRes
import org.telegram.messenger.R

enum class CameraType {

    FRONT, BACK, ALWAYS_ASK;

    @get:StringRes
    val title: Int
        get() = when (this) {
            ALWAYS_ASK -> R.string.CameraAsk
            FRONT -> R.string.CameraFront
            BACK -> R.string.CameraRear
        }
}