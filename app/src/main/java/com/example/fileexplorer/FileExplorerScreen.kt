package com.example.fileexplorer.ui

import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.data.FileRepository
import com.example.fileexplorer.viewModels.FileExplorerViewModel
import com.example.fileexplorer.viewModels.FileExplorerViewModelFactory

@Composable
fun FileExplorerScreen() {
    // Dependency injection or repository initialization
    val fileRepository = FileRepository()

    // ViewModel initialization with factory
    val fileExplorerViewModel: FileExplorerViewModel = viewModel(
        factory = FileExplorerViewModelFactory(fileRepository)
    )

    // Load files when the composable enters the composition
    LaunchedEffect(Unit) {
        fileExplorerViewModel.loadFiles(Environment.getExternalStorageDirectory().path)
    }

    // Collect state from ViewModel
    val files = fileExplorerViewModel.fileItems.collectAsState().value

//    // Compose a list of files
//    LazyColumn {
//        items(files) { fileItem ->
//            FileItemView(fileItem)
//        }
//    }

    // Get current directory for displaying the back button conditionally
    val currentDirectory = fileExplorerViewModel.currentDirectory.collectAsState().value

    Column {
        // Only show back button if not in root directory
        if (currentDirectory != Environment.getExternalStorageDirectory().path) {
            Button(onClick = { fileExplorerViewModel.navigateUp() }) {
                Text("Back")
            }
        }

        LazyColumn {
            items(files) { fileItem ->
                FileItemView(fileItem) {
                    if (fileItem.isDirectory) {
                        fileExplorerViewModel.navigateToDirectory(fileItem.path)
                    }
                }
            }
        }
    }
}

@Composable
fun FileItemView(fileItem: FileItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Choose an icon based on whether the item is a file or a directory
        val icon = if (fileItem.isDirectory) Icons.Filled.Folder else Icons.Filled.InsertDriveFile

        Icon(
            imageVector = icon,
            contentDescription = if (fileItem.isDirectory) "Directory" else "File"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = fileItem.name)
    }
}
