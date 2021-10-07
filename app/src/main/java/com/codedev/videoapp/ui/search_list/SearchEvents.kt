package com.codedev.videoapp.ui.search_list

sealed class SearchEvents {
    data class TextChangeListener(val query: String): SearchEvents()
    object Submit: SearchEvents()

}