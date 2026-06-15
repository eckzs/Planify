package com.app.planify.logic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    private const val MAX_DIMENSION = 1024
    private const val JPEG_QUALITY = 60

    // Reduce y comprime la imagen a JPEG, luego la codifica en Base64 para
    // guardarla como texto en Firestore (limite ~1MB por documento).
    fun uriToBase64(context: Context, uri: Uri): String? {
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        } ?: return null

        val ratio = minOf(
            MAX_DIMENSION.toFloat() / bitmap.width,
            MAX_DIMENSION.toFloat() / bitmap.height,
            1f
        )
        val scaled = if (ratio < 1f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).toInt(),
                (bitmap.height * ratio).toInt(),
                true
            )
        } else bitmap

        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
        return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
    }

    fun base64ToBytes(data: String): ByteArray? =
        runCatching { Base64.decode(data, Base64.NO_WRAP) }.getOrNull()
}
