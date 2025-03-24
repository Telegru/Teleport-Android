package ru.tusco.messenger.settings

object DahlAppearanceSettings {

    //Todo: move from DahlSettings.kt

    @JvmStatic
    var wallpaperAsBackground: Boolean
        get() = DahlSettings.getBoolean("wallpaper_as_background", true)
        set(value) {
            DahlSettings.putBoolean("wallpaper_as_background", value)
        }

    @JvmStatic
    var avatarAsBackground: Boolean
        get() = DahlSettings.getBoolean("avatar_as_background", false)
        set(value) {
            DahlSettings.putBoolean("avatar_as_background", value)
        }
}