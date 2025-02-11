package ru.tusco.messenger

import android.graphics.Bitmap
import android.graphics.Color
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.R
import org.telegram.messenger.SvgHelper
import org.telegram.tgnet.TLRPC.WallPaper
import ru.tusco.messenger.DefaultWallpapersHelper.KAZAN_WALLPAPER_PATH
import ru.tusco.messenger.DefaultWallpapersHelper.MOSCOW_WALLPAPER_PATH
import ru.tusco.messenger.DefaultWallpapersHelper.PITER_WALLPAPER_PATH
import ru.tusco.messenger.DefaultWallpapersHelper.RUSSIA_WALLPAPER_PATH
import java.io.FileOutputStream

object DefaultWallpapersHelper {

    val RUSSIA_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/russia.png"
    val KAZAN_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/kazan.png"
    val PITER_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/piter.png"
    val MOSCOW_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/moscow.png"

    fun createWallpaperFiles() {
        for (wallpaper in customWallpapers) {
            val patternBitmap = SvgHelper.getBitmap(
                wallpaper.svg,
                AndroidUtilities.dp(360f),
                AndroidUtilities.dp(640f),
                Color.WHITE
            )
            var stream: FileOutputStream?
            try {
                stream = FileOutputStream(wallpaper.path)
                val bitmap: Bitmap = patternBitmap.copy(Bitmap.Config.ARGB_8888, true)
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                bitmap.recycle()
                stream.close()
            } catch (e: Exception) {
                FileLog.e(e)
                e.printStackTrace()
            }
        }
    }

    fun createWallpaperModel() {
        for (wallpaper in customWallpapers) {
            //val wallpaper = WallPaper()
        }
    }
}

private val customWallpapers = listOf(CustomWallpaper.Russia, CustomWallpaper.Kazan, CustomWallpaper.Piter, CustomWallpaper.Moscow)


sealed class CustomWallpaper(val path: String, val svg: Int) {
    data object Russia: CustomWallpaper(RUSSIA_WALLPAPER_PATH, R.raw.dahl_wallpaper)
    data object Kazan: CustomWallpaper(KAZAN_WALLPAPER_PATH, R.raw.dahl_wallpaper_kazan)
    data object Piter: CustomWallpaper(PITER_WALLPAPER_PATH, R.raw.dahl_wallpaper_piter)
    data object Moscow: CustomWallpaper(MOSCOW_WALLPAPER_PATH, R.raw.dahl_wallpaper_moscow)
}