package com.example.fileexplorer.data

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val lastModified: Long, // Added the lastModified property
    val fileType: String // Added the fileType property
)

