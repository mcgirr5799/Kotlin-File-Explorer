package com.example.fileexplorer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Folder
import androidx.compose.material3.icons.filled.InsertDriveFile
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fileexplorer.data.FileItem
import com.example.fileexplorer.viewModels.FileExplorerViewModel

@Composable
fun FileExplorerScreen(viewModel: FileExplorerViewModel = viewModel()) {  // Corrected here
    val files = viewModel.files.collectAsState()

    LazyColumn {
        items(files.value) { fileItem ->
            FileItemView(fileItem)
        }
    }
}

@Composable
fun FileItemView(fileItem: FileItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (fileItem.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
            contentDescription = if (fileItem.isDirectory) "Folder" else "File"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = fileItem.name)
    }
}
