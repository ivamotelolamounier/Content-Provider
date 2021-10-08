package com.ivamotelo.applicationcontentprovider

import android.database.Cursor

/**
 * Interface responsável por gerenciar os eventos de click nos botões das UI
 */
interface NoteClickedListener {
    fun noteClickedItem(cursor: Cursor)
    fun noteRemoveItem(cursor: Cursor?)
}