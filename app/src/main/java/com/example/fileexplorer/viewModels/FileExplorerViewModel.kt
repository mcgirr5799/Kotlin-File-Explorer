package com.example.fileexplorer.viewModels

import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.data.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class FileExplorerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val fileRepository: FileRepository
) : ViewModel() {
    companion object {
        private const val KEY_CURRENT_DIRECTORY = "current_directory"
    }

    private val _fileItems = MutableStateFlow<List<FileItem>>(emptyList())
    val fileItems: StateFlow<List<FileItem>> = _fileItems

    private val _currentDirectory = MutableStateFlow(
        savedStateHandle.get<String>(KEY_CURRENT_DIRECTORY)
            ?: Environment.getExternalStorageDirectory().path
    )
    val currentDirectory: StateFlow<String> = _currentDirectory

    init {
        loadFiles(_currentDirectory.value)
    }

    fun navigateToDirectory(directoryPath: String) {
        _currentDirectory.value = directoryPath
        savedStateHandle.set(KEY_CURRENT_DIRECTORY, directoryPath) // Save the state
        loadFiles(directoryPath)
    }

    fun navigateUp() {
        val parent = File(_currentDirectory.value).parent ?: return
        navigateToDirectory(parent)
    }

    fun loadFiles(directoryPath: String) {
        viewModelScope.launch {
            _fileItems.value = fileRepository.getFilesInDirectory(directoryPath)
        }
    }
}
