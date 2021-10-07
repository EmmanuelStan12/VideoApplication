package com.codedev.videoapp.ui.search_list

import com.codedev.videoapp.data.room.AutoCompleteItem

sealed class SearchEvents {
    data class TextChangeListener(val query: String): SearchEvents()
    data class Submit(val query: String): SearchEvents()
    object GetAllQueries: SearchEvents()
    data class InsertQuery(val query: String): SearchEvents()
    data class DeleteQuery(val item: AutoCompleteItem): SearchEvents()

}