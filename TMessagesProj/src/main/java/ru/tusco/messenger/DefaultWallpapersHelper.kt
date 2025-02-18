package ru.tusco.messenger

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.annotation.RawRes
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.FileLog
import org.telegram.messenger.R
import org.telegram.messenger.SvgHelper
import org.telegram.tgnet.TLRPC
import org.telegram.tgnet.TLRPC.TL_document
import ru.tusco.messenger.DefaultWallpapersHelper.KAZAN_WALLPAPER_PATH
import ru.tusco.messenger.DefaultWallpapersHelper.MOSCOW_WALLPAPER_PATH
import ru.tusco.messenger.DefaultWallpapersHelper.PITER_WALLPAPER_PATH
import ru.tusco.messenger.DefaultWallpapersHelper.RUSSIA_WALLPAPER_PATH
import java.io.File
import java.io.FileOutputStream

object DefaultWallpapersHelper {

    val RUSSIA_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/russia.png"
    val KAZAN_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/kazan.png"
    val PITER_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/piter.png"
    val MOSCOW_WALLPAPER_PATH = ApplicationLoader.getFilesDirFixed().path + "/moscow.png"

    fun createWallpaperFiles() {
        for (wallpaper in DahlWallpaper.items) {
            val patternBitmap = SvgHelper.getBitmap(
                wallpaper.svg,
                AndroidUtilities.dp(360f),
                AndroidUtilities.dp(640f),
                Color.BLACK
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
                Log.w("DefaultWallpapersHelper", "Create wallpaper ${wallpaper.slug} error.", e)
            }
        }
    }
}

sealed class DahlWallpaper(val slug: String, val path: String, @RawRes val svg: Int, val colors: IntArray, val darkColors: IntArray) {

    data object Russia : DahlWallpaper(
        "slugRussia",
        RUSSIA_WALLPAPER_PATH,
        R.raw.dahl_wallpaper_russia,
        intArrayOf(
            0xFFEAA36E.toInt(),
            0xFFF0E486.toInt(),
            0xFFF29EBF.toInt(),
            0xFFE8C06E.toInt()
        ),
        intArrayOf(
            0xFF8ADBF2.toInt(),
            0xFF888DEC.toInt(),
            0xFFE39FEA.toInt(),
            0xFF679CED.toInt()
        )
    )

    data object Kazan : DahlWallpaper(
        "slugKazan",
        KAZAN_WALLPAPER_PATH,
        R.raw.dahl_wallpaper_kazan,
        intArrayOf(
            0xFF7FC289.toInt(),
            0xFFE4D573.toInt(),
            0xFFAFD677.toInt(),
            0xFFF0C07A.toInt()
        ),
        intArrayOf(
            0xFF7FA381.toInt(),
            0xFFFFF5C5.toInt(),
            0xFF336F55.toInt(),
            0xFFFBE37D.toInt()
        )
    )

    data object Piter : DahlWallpaper(
        "slugSaintPetersburg",
        PITER_WALLPAPER_PATH,
        R.raw.dahl_wallpaper_piter,
        intArrayOf(
            0xFFDAEACB.toInt(),
            0xFFECCBFF.toInt(),
            0xFFECCBFF.toInt(),
            0xFFB9E2FF.toInt()
        ),
        intArrayOf(
            0xFFFEC496.toInt(),
            0xFF962FBF.toInt(),
            0xFF4F5BD5.toInt(),
            0xFFFEC496.toInt()
        )

    )

    data object Moscow : DahlWallpaper(
        "slugMoscow",
        MOSCOW_WALLPAPER_PATH,
        R.raw.dahl_wallpaper_moscow,
        intArrayOf(
            0xFF7FC289.toInt(),
            0xFFECCBFF.toInt(),
            0xFFF29EBF.toInt(),
            0xFFF0C07A.toInt()
        ),

        intArrayOf(
            0xFF7FA381.toInt(),
            0xFF888DEC.toInt(),
            0xFFE39FEA.toInt(),
            0xFFFEC496.toInt()
        )
    )

    fun getColors(isDarkTheme: Boolean): IntArray = if (isDarkTheme) darkColors else colors


    companion object {

        // без lazy Russia принимает значение null. https://stackoverflow.com/a/53608393
        val items by lazy { arrayOf(Russia, Kazan, Piter, Moscow) }

        val slugs by lazy { items.map { it.slug }.toSet() }

        @JvmStatic
        fun getPatterns(isDarkTheme: Boolean): List<TLRPC.TL_wallPaper> = items.map { dw ->
            val wallpaper = TLRPC.TL_wallPaper()
            wallpaper.id = fileId(dw)
            wallpaper.access_hash = accessHash(dw)
            wallpaper.pattern = true
            wallpaper.isDefault = true
            wallpaper.dark = isDarkTheme
            wallpaper.slug = dw.slug
            val document = TL_document()
            document.flags = 1
            document.id = fileId(dw)
            document.mime_type = "image/png"
            document.size = File(dw.path).length()
            val imageSize = TLRPC.TL_documentAttributeImageSize().apply {
                w = AndroidUtilities.dp(360f)
                h = AndroidUtilities.dp(640f)
            }

            val fileName = TLRPC.TL_documentAttributeFilename().apply {
                file_name = File(dw.path).name
            }
            document.attributes = arrayListOf(imageSize, fileName)
            val thumb = TLRPC.TL_photoSize()
            thumb.w = 155
            thumb.h = 320
            thumb.size = document.size.toInt()
            document.thumbs = arrayListOf(thumb)
            document.video_thumbs = arrayListOf()
            document.localPath = dw.path
            wallpaper.document = document
            return@map wallpaper
        }

        private fun fileId(dw: DahlWallpaper): Long = when (dw) {
            Kazan -> 231222124451
            Russia -> 241222124562
            Piter -> 251222124673
            Moscow -> 223442223441
        }

        private fun accessHash(dw: DahlWallpaper): Long = when (dw) {
            Kazan -> 123444
            Russia -> 223555
            Piter -> 323666
            Moscow -> 533112
        }
    }
}