package ru.tusco.messenger.settings

object DahlChatsSettings {

    //Todo move from DahlSettings.kt

    @JvmStatic
    var isKeyboardHidingEnabled: Boolean
        get() = DahlSettings.getBoolean("keyboard_hiding_enabled", false)
        set(value) {
            DahlSettings.putBoolean("keyboard_hiding_enabled", value)
        }

    @JvmStatic
    var fullscreenInputEnabled: Boolean
        get() = DahlSettings.getBoolean("fullscreen_input_enabled", false)
        set(value) {
            DahlSettings.putBoolean("fullscreen_input_enabled", value)
        }

}