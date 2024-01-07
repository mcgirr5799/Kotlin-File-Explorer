package com.example.fileexplorer.data

import android.util.Log
import java.io.File

open class FileRepository {

    open fun getFilesInDirectory(directoryPath: String): List<FileItem> {
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
                    fileType = file.extension, // Added the fileType property
                    fileSize = file.length() // Added the fileSize property
                )
            } ?: emptyList()
        } else {
            Log.d("FileRepository", "Directory not found or not a directory")
            emptyList()
        }
    }

    fun searchFilesWithCriteria(directoryPath: String, criteria: SearchCriteria): List<FileItem> {
        val directory = File(directoryPath)
        return directory.listFiles()?.filter { file ->
            matchesCriteria(file, criteria)
        }?.map { file ->
            Log.d("FileRepository", "File found: ${file.name}, isDirectory: ${file.isDirectory}")
            FileItem(
                name = file.name,
                path = file.path,
                isDirectory = file.isDirectory,
                lastModified = file.lastModified(), // Added the lastModified property
                fileType = file.extension, // Added the fileType property
                fileSize = file.length() // Added the fileSize property
            )
        } ?: emptyList()
    }

    private fun matchesCriteria(file: File, criteria: SearchCriteria): Boolean {
        if (criteria.query.isNotEmpty() && !file.name.contains(criteria.query, ignoreCase = true)) {
            return false
        }
        if (criteria.fileType.isNotEmpty() && file.extension != criteria.fileType) {
            return false
        }
        if (file.length() < criteria.minFileSize || file.length() > criteria.maxFileSize) {
            return false
        }
        return true
    }
}

