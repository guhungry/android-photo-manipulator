package com.guhungry.photomanipulator

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.guhungry.photomanipulator.factory.AndroidFactory
import com.guhungry.photomanipulator.factory.AndroidConcreteFactory
import com.guhungry.photomanipulator.helper.AndroidFile
import com.guhungry.photomanipulator.helper.AndroidConcreteFile
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

object FileUtils {
    private val LOCAL_URI_PREFIXES = arrayOf(ContentResolver.SCHEME_FILE, ContentResolver.SCHEME_CONTENT, ContentResolver.SCHEME_ANDROID_RESOURCE)
    private const val BASE64_URI_PREFIX = "data:"

    /**
     * Check if uri is local or web
     */
    private fun isLocalUri(uri: String): Boolean = LOCAL_URI_PREFIXES.any { uri.startsWith(it) }

    /**
     * Create a temporary file in the cache directory on either internal or external storage,
     * whichever is available and has more free space.
     *
     * @param mimeType the MIME type of the file to create (image/\*)
     */
    @JvmStatic
    fun createTempFile(context: Context, prefix: String, mimeType: String?, file: AndroidFile = AndroidConcreteFile()): File {
        return file.createTempFile(prefix, MimeUtils.toExtension(mimeType), cachePath(context))
    }

    @JvmStatic
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
     * Delete all files in directory that start with prefix
     */
    @JvmStatic
    fun cleanDirectory(directory: File, prefix: String) {
        directory
            .listFiles { _, name -> name.startsWith(prefix) }
            ?.forEach { it.delete() }
    }

    @JvmStatic
    fun saveImageFile(image: Bitmap, mime: String, quality: Int, target: File, file: AndroidFile = AndroidConcreteFile()) {
        file.makeFileOutputStream(target).use {
            image.compress(MimeUtils.toCompressFormat(mime), quality, it)
        }
    }

    /**
     * Open bitmap input stream from URI.
     *
     * Supports local URIs (file://, content://, android.res://), base64 data URIs,
     * and remote URLs (http://, https://).
     *
     * Warning: Remote URL fetching performs synchronous network I/O and should not be
     * called on the main/UI thread. Use background thread or coroutines for remote URLs
     * to avoid ANR (Application Not Responding) errors.
     *
     * @param context Android context
     * @param uri URI of the image
     * @param factory (Optional) Factory for creating Android Objects for Testing
     * @return InputStream of the bitmap
     * @throws IOException if the bitmap cannot be opened
     */
    @JvmStatic
    fun openBitmapInputStream(context: Context, uri: String, factory: AndroidFactory = AndroidConcreteFactory()): InputStream {
        if (isBase64Data(uri)) {
            val data = uri.substring(uri.indexOf(",") + 1)
            return ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT))
        }
        if (isLocalUri(uri)) {
            return context.contentResolver.openInputStream(factory.makeUri(uri))
                ?: throw IOException("Cannot open bitmap: $uri")
        }
        return factory.fetchUrl(uri)
    }

    private fun isBase64Data(uri: String): Boolean = uri.startsWith(BASE64_URI_PREFIX)
}