package com.example.fileexplorer.data

import android.util.Log
import java.io.File

class FileRepository {

    fun getFilesInDirectory(directoryPath: String): List<FileItem> {
        val directory = File(directoryPath)
        Log.d("FileRepository", "Accessing directory: $directoryPath")

        return if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.map { file ->
                Log.d("FileRepository", "File found: ${file.name}, isDirectory: ${file.isDirectory}")
                FileItem(
                    name = file.name,
                    path = file.path,
                    isDirectory = file.isDirectory,
                    lastModified = file.lastModified(), // Added the lastModified property
                    fileType = file.extension // Added the fileType property
                )
            } ?: emptyList()
        } else {
            Log.d("FileRepository", "Directory not found or not a directory")
            emptyList()
        }
    }
}

