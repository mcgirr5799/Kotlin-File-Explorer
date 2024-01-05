package com.example.fileexplorer.ui

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.data.FileRepository
import com.example.fileexplorer.viewModels.FileExplorerViewModel
import com.example.fileexplorer.viewModels.FileExplorerViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
// Please note that this is a simplified version of the code and may not include all the necessary imports and classes. You need to ensure that all referenced functions and classes are properly imported and exist.

@Composable
fun FileExplorerScreen() {
    val expanded = remember { mutableStateOf(false) }
    val sortOptions = listOf("Name", "Date", "Type", "Size")
    val selectedOption = remember { mutableStateOf(sortOptions[0]) }
    val showSortingDialog = remember { mutableStateOf(false) }

    val fileRepository = FileRepository()
    val fileExplorerViewModel: FileExplorerViewModel = viewModel(
        factory = FileExplorerViewModelFactory(fileRepository, LocalSavedStateRegistryOwner.current)
    )

    val files = fileExplorerViewModel.fileItems.collectAsState().value
    val currentDirectory = fileExplorerViewModel.currentDirectory.collectAsState().value

    val context = LocalContext.current
    val openDirectoryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            fileExplorerViewModel.loadFilesFromUri(context, it)
            fileExplorerViewModel.updateCurrentDirectoryFromUri(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("File Explorer") },
                actions = {
                    IconButton(onClick = { showSortingDialog.value = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                if (currentDirectory != Environment.getExternalStorageDirectory().path) {
                    FloatingActionButton(onClick = { fileExplorerViewModel.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Navigate up"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                FloatingActionButton(onClick = { openDirectoryLauncher.launch(null) }) {
                    Icon(Icons.Filled.FolderOpen, contentDescription = "Open Directory")
                }
            }
        },
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(files) { fileItem ->
                    FileItemView(fileItem) {
                        if (fileItem.isDirectory) {
                            Log.d("FileExplorerScreen", "Navigating to ${fileItem.path}, is directory: ${fileItem.isDirectory}")
                            fileExplorerViewModel.navigateToDirectory(fileItem.path)
                        }
                    }
                }
            }
        }
    )

    if (showSortingDialog.value) {
        SortingDialog(showSortingDialog, onOptionSelected = { option ->
            fileExplorerViewModel.sortFiles(option)
        }, sortOptions)
    }
}
@Composable
fun FileItemView(fileItem: FileItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (fileItem.isDirectory) Icons.Filled.Folder
                    else if (fileItem.fileType == "jpg" || fileItem.fileType == "png" || fileItem.fileType == "jpeg") Icons.Filled.Image
                    else if (fileItem.fileType == "mp4") Icons.Filled.VideoLibrary
                    else if (fileItem.fileType == "mp3") Icons.Filled.MusicNote
                    else Icons.Filled.InsertDriveFile,
                contentDescription = if (fileItem.isDirectory) "Directory" else "File",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileItem.name,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(
                        text = "Last modified: ${formatDate(fileItem.lastModified)}",
                        color = Color.Gray,
                        maxLines = 1,
                        modifier = Modifier.weight(1f) // Allocate space proportionally
                    )

                    if (!fileItem.isDirectory) {
                        Text(
                            text = "Type: ${fileItem.fileType}",
                            color = Color.Gray,
                            maxLines = 1,
                            modifier = Modifier.weight(1f) // Allocate space proportionally
                        )
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun SortingDialog(showDialog: MutableState<Boolean>, onOptionSelected: (String) -> Unit, sortOptions: List<String>) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Sort By") },
            text = {
                Column {
                    sortOptions.forEach { option ->
                        TextButton(onClick = {
                            onOptionSelected(option)
                            showDialog.value = false
                        }) {
                            Text(option)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text("Close")
                }
            }
        )
    }
}
