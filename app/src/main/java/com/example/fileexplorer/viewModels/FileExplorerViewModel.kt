package com.example.fileexplorer.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.data.FileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FileExplorerViewModel(private val fileRepository: FileRepository) : ViewModel() {
    private val _files = MutableStateFlow<List<FileItem>>(emptyList())
    val files: StateFlow<List<FileItem>> = _files

    init {
        loadFiles("/path/to/directory") // Replace with actual path
    }

    private fun loadFiles(directoryPath: String) {
        viewModelScope.launch {
            _files.value = fileRepository.getFilesInDirectory(directoryPath)
        }
    }
}
