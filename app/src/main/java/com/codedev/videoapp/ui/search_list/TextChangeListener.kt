package com.codedev.videoapp.ui.search_list

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.addTextChangeListener(
    onChange: (String) -> Unit
) {
    this.addTextChangedListener(object: TextWatcher {
        override fun beforeTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            onChange(editable.toString())
        }
    })
}