package com.guhungry.photomanipulator.helper

import java.io.File
import java.io.FileOutputStream

interface AndroidFile {
    fun createTempFile(prefix: String, suffix: String?, directory: File?): File
    fun makeFileOutputStream(target: File): FileOutputStream
}

class AndroidConcreteFile: AndroidFile {
    override fun createTempFile(prefix: String, suffix: String?, directory: File?): File = File.createTempFile(prefix, suffix, directory)
    override fun makeFileOutputStream(target: File): FileOutputStream = FileOutputStream(target)
}