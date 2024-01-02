package com.example.fileexplorer.data

import java.io.File

class FileRepository {

    fun getFilesInDirectory(directoryPath: String): List<FileItem> {
        val directory = File(directoryPath)
        return if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.map { file ->
                FileItem(
                    name = file.name,
                    path = file.path,
                    isDirectory = file.isDirectory
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }
}

