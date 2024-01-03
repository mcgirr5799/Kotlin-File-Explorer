package com.example.fileexplorer.viewModels

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.fileexplorer.data.FileRepository

class FileExplorerViewModelFactory(
    private val fileRepository: FileRepository,
    owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(FileExplorerViewModel::class.java)) {
            return FileExplorerViewModel(handle, fileRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
