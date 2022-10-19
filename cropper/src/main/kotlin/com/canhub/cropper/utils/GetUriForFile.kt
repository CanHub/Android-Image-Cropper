package com.canhub.cropper.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.canhub.cropper.common.CommonValues
import com.canhub.cropper.common.CommonVersionCheck
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This class exist because of two issues. One is related to the new Scope Storage for OS 10+
 * Where we should not access external storage anymore. Because of this we cannot get a external uri
 *
 * Using FileProvider to retrieve the path can return a value that is not the real one for some devices
 * This happen in specific devices and OSs. Because of this is needed to do a lot of if/else and
 * try/catch to just use the latest cases when need.
 *
 * This code is not good, but work. I don't suggest anyone to reproduce it.
 *
 * Most of the devices will work fine, but if you worry about memory usage, please remember to clean
 * the cache from time to time,
 */
internal fun getUriForFile(context: Context, file: File): Uri {
    val authority = context.packageName + CommonValues.authority
    try {
        Log.i("AIC", "Try get URI for scope storage - content://")
        return FileProvider.getUriForFile(context, authority, file)
    } catch (e: Exception) {
        try {
            Log.e("AIC", "${e.message}")
            Log.w(
                "AIC",
                "ANR Risk -- Copying the file the location cache to avoid 'external-files-path' bug for N+ devices"
            )
            // Note: Periodically clear this cache
            val cacheFolder = File(context.cacheDir, CommonValues.CROP_LIB_CACHE)
            val cacheLocation = File(cacheFolder, file.name)
            var input: InputStream? = null
            var output: OutputStream? = null
            try {
                input = FileInputStream(file)
                output = FileOutputStream(cacheLocation) // appending output stream
                input.copyTo(output)
                Log.i(
                    "AIC",
                    "Completed Android N+ file copy. Attempting to return the cached file"
                )
                return FileProvider.getUriForFile(context, authority, cacheLocation)
            } catch (e: Exception) {
                Log.e("AIC", "${e.message}")
                Log.i("AIC", "Trying to provide URI manually")
                val path = "content://$authority/files/my_images/"

                if (CommonVersionCheck.isAtLeastO26()) {
                    Files.createDirectories(Paths.get(path))
                } else {
                    val directory = File(path)
                    if (!directory.exists()) directory.mkdirs()
                }

                return Uri.parse("$path${file.name}")
            } finally {
                input?.close()
                output?.close()
            }
        } catch (e: Exception) {
            Log.e("AIC", "${e.message}")

            if (!CommonVersionCheck.isAtLeastQ29()) {
                val cacheDir = context.externalCacheDir
                cacheDir?.let {
                    try {
                        Log.i(
                            "AIC",
                            "Use External storage, do not work for OS 29 and above"
                        )
                        return Uri.fromFile(File(cacheDir.path, file.absolutePath))
                    } catch (e: Exception) {
                        Log.e("AIC", "${e.message}")
                    }
                }
            }
            // If nothing else work we try
            Log.i("AIC", "Try get URI using file://")
            return Uri.fromFile(file)
        }
    }
}
