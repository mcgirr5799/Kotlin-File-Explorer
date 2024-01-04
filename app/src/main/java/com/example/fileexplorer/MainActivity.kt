package com.example.fileexplorer

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.fileexplorer.data.Permissions
import com.example.fileexplorer.ui.FileExplorerScreen
import com.example.fileexplorer.ui.theme.FileExplorerTheme

class MainActivity : ComponentActivity() {
    private val permissions = Permissions(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissions.checkAndRequestPermissions()

        setContent {
            FileExplorerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    FileExplorerScreen()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            Permissions.PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Log.e("MainActivity", "All requested permissions granted")
                } else {
                    Log.e("MainActivity", "One or more permissions denied")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}


