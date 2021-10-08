/**
 * Para utilizar o contentProvider, é necessário instânciar o LoaderManager no construtor da MainActivity
 * sua função é sempre fazer a busca em segundo plano do Cursor, evitando erros de threads na aplicação.
 * Deve-se instânciar a 'NoteRecycleView do tipo RecyclerView', bem como,
 * o 'noteAdd do tipo FloatingAcitionButton'
 * Cria-se as instâncias dos objetos UI, adapter e Botão flutuante com suas respectivas ações
 * Também é necessário a criação do 'Adapter' da RecyclerView
 * Captura o click do ID da recyclerView, do resultado fornecido pelo cursor
 * Método para chamar o "NotesDetailsFragment"
 * Feitas todas as implementações, estando todas variáveis instânciadas, é NECESSÀRIO INICIAR os métodos
 * implementados abaixo, para que funcionem em SEGUNDO PLANO, onde o contentProvider irá trabalhar, sem
 * afetar a performace do aplicativo
 * Método Loader para instânciar o que se busca, no caso em tela será a pesquisa no contentProvider
 * o método CursorLoad() já possui todos os argumentos necessários para se construir uma pesquisa
 *'URI_NOTES' : "content://com.ivamotelo.applicationcontentprovider.provider/notes', trazendo todas
 * as notas que estejam no contentProvider
 * Método utilizado para manipular os dados obtidos pelo CreateLoad
 * Método para finalizar a pesquisa em segundo plano realizada pelo método principal LoaderManager()
 */

package com.ivamotelo.applicationcontentprovider

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns._ID
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ivamotelo.applicationcontentprovider.database.NotesDataBaseHelper.Companion.TITLE_NOTES
import com.ivamotelo.applicationcontentprovider.database.NotesProvider.Companion.URI_NOTES

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    lateinit var noteRecyclerView: RecyclerView
    lateinit var noteAdd: FloatingActionButton
    lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteAdd = findViewById(R.id.note_add)
        noteAdd.setOnClickListener {
            NotesDetailsFragment().show(supportFragmentManager, "dialog")
        }
        adapter = NotesAdapter(object : NoteClickedListener {
            @SuppressLint("Range")
            override fun noteClickedItem(cursor: Cursor) {
                val id = cursor.getLong(cursor.getColumnIndex(_ID))
                val fragment = NotesDetailsFragment.newInstance(id)
                fragment.show(supportFragmentManager, "dialog")
            }

            @SuppressLint("Range")
            override fun noteRemoveItem(cursor: Cursor?) {
                val id = cursor?.getLong(cursor.getColumnIndex(_ID))
                contentResolver.delete(Uri.withAppendedPath(URI_NOTES, id.toString()), null, null)
            }
        })
        adapter.setHasStableIds(true)   // método para garantir que não existem 'ids' repetidos dentro do adapter

        noteRecyclerView = findViewById(R.id.notes_recycler)
        noteRecyclerView.layoutManager = LinearLayoutManager(this)
        noteRecyclerView.adapter = adapter

        LoaderManager.getInstance(this).initLoader(0, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        CursorLoader(this, URI_NOTES, null, null, null, TITLE_NOTES)

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null) {
            adapter.setCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.setCursor(null)
    }
}