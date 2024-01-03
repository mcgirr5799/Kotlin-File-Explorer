package com.example.fileexplorer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fileexplorer.data.FileRepository
import com.example.fileexplorer.viewModels.FileExplorerViewModel

class FileExplorerViewModelFactory(private val fileRepository: FileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FileExplorerViewModel::class.java)) {
            return FileExplorerViewModel(fileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}