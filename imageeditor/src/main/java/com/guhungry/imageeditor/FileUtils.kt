package com.guhungry.imageeditor

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    private val LOCAL_URI_PREFIXES = arrayOf("file://", "content://")

    /**
     * Check if uri is local or web
     */
    fun isLocalFile(uri: String): Boolean = LOCAL_URI_PREFIXES.any { uri.startsWith(it) }

    /**
     * Create a temporary file in the cache directory on either internal or external storage,
     * whichever is available and has more free space.
     *
     * @param mimeType the MIME type of the file to create (image/\*)
     */
    fun createTempFile(context: Context, prefix: String, mimeType: String?): File {
        return File.createTempFile(prefix, MimeUtils.toExtension(mimeType), cachePath(context))
    }

    fun cachePath(context: Context): File? {
        val externalCacheDir = context.externalCacheDir
        val internalCacheDir = context.cacheDir

        if (externalCacheDir == null && internalCacheDir == null) {
            throw IOException("No cache directory available")
        }
        return when {
            externalCacheDir == null -> internalCacheDir
            internalCacheDir == null -> externalCacheDir
            else -> if (externalCacheDir.freeSpace > internalCacheDir.freeSpace)
                externalCacheDir
            else
                internalCacheDir
        }
    }

    /**
     * Delete dll files in directory that starts with prefix
     */
    fun cleanDirectory(directory: File, prefix: String) {
        directory
            .listFiles { _, name -> name.startsWith(prefix) }
            .forEach { it.delete() }
    }

    fun saveImageFile(image: Bitmap, mime: String, quality: Int, target: File) {
        FileOutputStream(target).use {
            image.compress(MimeUtils.toCompresFormat(mime), quality, it);
        }
    }
}