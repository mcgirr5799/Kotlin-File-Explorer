package com.example.fileexplorer.data

data class SearchCriteria(
    var query: String = "",
    var fileType: String = "", // Add other properties as needed
    var minFileSize: Long = 0, // Minimum file size in bytes
    var maxFileSize: Long = Long.MAX_VALUE // Maximum file size in bytes
)