package com.hariomahlawat.bannedappdetector.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

/** Return a copy of this bitmap with the provided label drawn in the bottom
 *  left corner. Used when sharing screenshots so the receiver can identify
 *  who sent it. */
fun Bitmap.withLabel(context: Context, label: String): Bitmap {
    val copy = copy(config ?: Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(copy)

    val dm = context.resources.displayMetrics
    val padding = (8 * dm.density).toInt()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 14f * dm.scaledDensity
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    val bgPaint = Paint().apply { color = Color.argb(160, 0, 0, 0) }

    val bounds = Rect()
    paint.getTextBounds(label, 0, label.length, bounds)
    val x = padding
    val extraTopOffset = (133 * dm.density).toInt()
    val y = padding + bounds.height() + extraTopOffset



    canvas.drawRect(
        (x - padding).toFloat(),
        (y - bounds.height() - padding).toFloat(),
        (x + bounds.width() + padding).toFloat(),
        (y + padding).toFloat(),
        bgPaint
    )
    canvas.drawText(label, x.toFloat(), y.toFloat(), paint)

    return copy
}

/** Save the bitmap to a cache file and return its content Uri */
fun Bitmap.saveToCache(context: Context): Uri {
    val file = File.createTempFile("scan_result_", ".png", context.cacheDir)
    FileOutputStream(file).use { out ->
        compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

/** Share the screenshot represented by the Uri */
fun shareImage(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, null))
}
