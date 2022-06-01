package com.example.materialwall.common

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider.getUriForFile
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LoadImageUtil @Inject constructor(
    private val appContext : Application
) {
    fun saveImage(image: Bitmap, id: String): Uri? {
        var savedImagePath: String? = null
        val imageFileName = "$id.jpg"
        var imageFile: File? = null
        val storageDir =
            File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/MaterialWall")
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath

            try {
                val fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 80, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            galleryAddPic(savedImagePath)

        }

        return getUriForFile(appContext, "com.example.materialwall.fileprovider", imageFile!!)
    }

    private fun galleryAddPic(imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        appContext.sendBroadcast(mediaScanIntent)
    }

}