package com.app.musicplayer.extentions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.onTextChangeListener(onTextChangedAction:(newText:String)->Unit) =
    addTextChangedListener(object :
    TextWatcher{
        override fun afterTextChanged(text: Editable?) {
            onTextChangedAction(text.toString())
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })