package com.charitylink.app
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

fun saveProfilePhoto(context: Context, uri: Uri, key: String): Boolean {
    return try {
        val input = context.contentResolver.openInputStream(uri) ?: return false
        val original = BitmapFactory.decodeStream(input)
        input.close()
        if (original == null) return false
        val side = minOf(original.width, original.height)
        val x = (original.width - side) / 2
        val y = (original.height - side) / 2
        val square = Bitmap.createBitmap(original, x, y, side, side)
        val scaled = Bitmap.createScaledBitmap(square, 400, 400, true)
        File(context.filesDir, "profile_$key.jpg").outputStream().use {
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
        true
    } catch (e: Exception) {
        false
    }
}

fun loadProfilePhoto(context: Context, key: String): Bitmap? {
    val f = File(context.filesDir, "profile_$key.jpg")
    return if (f.exists()) BitmapFactory.decodeFile(f.absolutePath) else null
}
