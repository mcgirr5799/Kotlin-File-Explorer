package com.example.fileexplorer.ui

import android.os.Environment
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
@Composable
fun FileExplorerScreen() {
    val fileRepository = FileRepository()
    val fileExplorerViewModel: FileExplorerViewModel = viewModel(
        factory = FileExplorerViewModelFactory(fileRepository, LocalSavedStateRegistryOwner.current)
    )

    // Collect state from ViewModel
    val files = fileExplorerViewModel.fileItems.collectAsState().value

    // Get current directory for displaying the back button conditionally
    val currentDirectory = fileExplorerViewModel.currentDirectory.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("File Explorer") })
        },
        floatingActionButton = {
            if (currentDirectory != Environment.getExternalStorageDirectory().path) {
                FloatingActionButton(onClick = { fileExplorerViewModel.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Navigate up"
                    )
                }
            }
        },
        content = { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(files) { fileItem ->
                    FileItemView(fileItem) {
                        // onClick logic for navigating directories or opening files
                        if (fileItem.isDirectory) {
                            Log.d("FileExplorerScreen", "Navigating to ${fileItem.path}, is directory: ${fileItem.isDirectory}")
                            fileExplorerViewModel.navigateToDirectory(fileItem.path)
                        }
                    }
                }
            }
        }
    )
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
