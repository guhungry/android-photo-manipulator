package com.guhungry.photomanipulator.helper

import java.io.File

interface AndroidFile {
    fun createTempFile(prefix: String, suffix: String?, directory: File?): File
}

class AndroidFileHelper: AndroidFile {
    override fun createTempFile(prefix: String, suffix: String?, directory: File?): File {
        return File.createTempFile(prefix, suffix, directory)
    }

}