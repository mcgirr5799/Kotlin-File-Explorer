package com.example.fileexplorer.viewModels

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.data.FileRepository
import com.example.fileexplorer.data.SearchCriteria
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
    private val _searchCriteria = MutableStateFlow(SearchCriteria())

    val fileItems: StateFlow<List<FileItem>> = _fileItems

    val searchCriteria: StateFlow<SearchCriteria> = _searchCriteria

    fun updateSearchCriteria(criteria: SearchCriteria) {
        _searchCriteria.value = criteria
        performSearch()
    }

    private fun performSearch() {
        viewModelScope.launch {
            val criteria = _searchCriteria.value
            _fileItems.value = fileRepository.searchFilesWithCriteria(_currentDirectory.value, criteria)
        }
    }

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

    fun loadFilesFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val directory = DocumentFile.fromTreeUri(context, uri)
            val files = directory?.listFiles()?.map { file ->
                FileItem(
                    name = file.name ?: "",
                    path = file.uri.toString(),
                    isDirectory = file.isDirectory,
                    lastModified = file.lastModified(),
                    fileType = file.type ?: "unknown", // Changed to use file.type
                    fileSize = file.length()
                )
            }
            _fileItems.value = files ?: emptyList()
        }
    }

    fun updateCurrentDirectoryFromUri(uri: Uri) {
        // Convert the Uri to a path or a representation that you can use
        val newPath = convertUriToPath(uri)
        _currentDirectory.value = newPath
    }

    private fun convertUriToPath(uri: Uri): String {
        // Implement logic to convert Uri to a usable path or identifier
        // This is complex as Uris from SAF do not directly map to file paths
        // You might store Uri.toString() or another identifier depending on your use case
        return uri.toString()
    }

    fun sortFiles(sortOption: String) {
        val sortedList = when (sortOption) {
            "Name" -> _fileItems.value.sortedBy { it.name }
            "Date" -> _fileItems.value.sortedByDescending { it.lastModified }
            "Type" -> _fileItems.value.sortedBy { it.fileType }
            "Size" -> _fileItems.value.sortedByDescending { it.fileSize }
            else -> _fileItems.value
        }
        _fileItems.value = sortedList
    }
}
