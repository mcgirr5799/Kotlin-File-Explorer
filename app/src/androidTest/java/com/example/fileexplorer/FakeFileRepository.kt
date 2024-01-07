package com.example.fileexplorer

import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.data.FileRepository

class FakeFileRepository : FileRepository() {
    // A mutable list to store 'fake' files
    private var fakeFiles: List<FileItem> = mutableListOf()

    // Override methods from FileRepository
    override fun getFilesInDirectory(directoryPath: String): List<FileItem> {
        // Return the fake list of files instead of performing real file system operations
        return fakeFiles
    }

    // A method to set fake files for testing
    fun setFakeFiles(files: List<FileItem>) {
        fakeFiles = files
    }

    // Other methods can be overridden similarly, depending on what you need for your tests
}