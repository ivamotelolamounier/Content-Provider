/**
 * Implementação dos métodos a serem utilizados pelo Adapter, e a criação de uma variável 'cursor'
 * que receberá os dados do método 'onLoadFinder()' da MainActivity
 * Verifica se o cursor é diferente de nulo, se verdadeiro, então ele adiciona a contagem de
 * itens ao contador, caso contrário, ele atribui zero ao contador
 * Função que recebe os dados que estão na pesquisa (sua posição), nomeando e passando os valores
 * para dentro das váriaveis dos objetos criados no layout
 * método para obter o valor que está dentro do cursor e tranfere para o objetos (TextView, Button, etc)
 * que está na UI, que consiste em obter o objeto 'Cursor', aplicar o método 'getString', e a COLUNA em que
 * se localiza o valor desejado
 * método quando se clica dentro do CardView
 * Instânciamento dos TextViews e Buttons da classe NotesAdapter
 */

package com.ivamotelo.applicationcontentprovider

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivamotelo.applicationcontentprovider.database.NotesDataBaseHelper.Companion.DESCRIPTION_NOTES
import com.ivamotelo.applicationcontentprovider.database.NotesDataBaseHelper.Companion.TITLE_NOTES

class NotesAdapter(private val listener: NoteClickedListener) : RecyclerView.Adapter<NotesViewHolder>() {

    private var mCursor: Cursor? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder =
        NotesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))

    override fun getItemCount(): Int = if (mCursor != null) mCursor?.count as Int else 0

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        mCursor?.moveToPosition(position)

        holder.noteTitle.text = mCursor?.getString(mCursor?.getColumnIndex(TITLE_NOTES) as Int)
        holder.noteDescription.text = mCursor?.getString(mCursor?.getColumnIndex(DESCRIPTION_NOTES) as Int)
        holder.noteButtonRemove.setOnClickListener{
            mCursor?.moveToPosition(position)
            listener.noteRemoveItem(mCursor)
            notifyDataSetChanged()
        }
        holder.itemView.setOnClickListener{ listener.noteClickedItem(mCursor as Cursor) }
    }


    fun setCursor(newCursor: Cursor?) {
        mCursor = newCursor     // popula a variável dentro do adapter
        notifyDataSetChanged()  // Após o povoamento da variável, será notificada a RecyclerView das nudanças
    }
}

class NotesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val noteTitle = itemView.findViewById(R.id.note_title) as TextView
    val noteDescription = itemView.findViewById(R.id.notes_description) as TextView
    val noteButtonRemove = itemView.findViewById(R.id.note_button_remove) as Button
}