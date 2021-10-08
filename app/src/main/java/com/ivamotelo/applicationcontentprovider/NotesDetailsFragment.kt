/**
 * Cria-se um object companation para facilitar sua inicialização na Activity
 * Verificação para certificar se chegou dados da activity, se positivo, então a variável
 * 'newNote' será verdadeira
 * Para fazer as pesquisas e retornar para as variáveis 'noteEditTitle' e 'NoteEditDescripition'
 * O cursor recebe o fragmento da Activity, junto com o objeto contentResolver, que faz uma query
 * do 'SELECT', basicamente com o endereço montado na variável 'uri'.
 * Recebido estes argumentos, é feita nova verificação na variável 'cursor'. Se for encontrado algum
 * dado no cursor,
 * E feita uma nova verificação para verificar a posição dos dados recebidos pelo cursor
 * O retorno será em forma de um Dialog, com o título de nova mensagem, caso seja para inserir
 * novos dados, ou de editar, se for para editar a mensagem recebida
 * Função responsável pelo 'input' dos dados da contentProvaider, que OBRIGATÒRIAMENTE é um dado
 * oriundo da classe 'ContentValues'. Ao ser instânciada, a mesma recebe uma CHAVE e um VALOR
 * a CHAVE será a coluna do DB que se deseja popular, e o VALOR será o conteúdo da variável
 * 'noteEditTitle', o mesmo ocorrendo com a coluna 'Description'
 * Após, será checado se o valor da ID é diferente de zero, confirmando a existência de dados
 * isso feito, é instânciada uma variáel para receber os dados para serem atualizados, já editados
 * através do método 'update' da context?.contentResolver?.update()
 * Caso contrário (else), não havendo dados para atualizar, será realizado um INSERT (input de dados)
 */

package com.ivamotelo.applicationcontentprovider

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.ivamotelo.applicationcontentprovider.database.NotesDataBaseHelper.Companion.DESCRIPTION_NOTES
import com.ivamotelo.applicationcontentprovider.database.NotesDataBaseHelper.Companion.TITLE_NOTES
import com.ivamotelo.applicationcontentprovider.database.NotesProvider.Companion.URI_NOTES

class NotesDetailsFragment : DialogFragment(), DialogInterface.OnClickListener {

    private lateinit var noteEditTitle: EditText
    private lateinit var noteEditDescription: EditText
    private var id: Long = 0L

    companion object {
        private const val EXTRA_ID = "id"
        fun newInstance(id: Long): NotesDetailsFragment {
            val bundle = Bundle()
            bundle.putLong(EXTRA_ID, id)

            val notesFragment = NotesDetailsFragment()
            notesFragment.arguments = bundle
            return notesFragment
        }
    }

    @SuppressLint("Range")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.note_datails, null)

        noteEditTitle = view?.findViewById(R.id.note_edt_title) as EditText
        noteEditDescription = view.findViewById(R.id.note_edt_description) as EditText

        var newNote = true
        if (arguments != null && arguments?.getLong(EXTRA_ID) != 0L) {
            id = arguments?.getLong(EXTRA_ID) as Long  //Cast da variável
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())  // montou a URI
            val cursor =
                activity?.contentResolver?.query(uri, null, null, null, null)

            if (cursor?.moveToNext() as Boolean) {
                newNote = false // como não são novas entradas de dados, o newNote será falso
                noteEditTitle.setText(cursor.getString(cursor.getColumnIndex(TITLE_NOTES)))
                noteEditDescription.setText(cursor.getString(cursor.getColumnIndex(DESCRIPTION_NOTES)))
            }
            cursor.close()
        }

        return AlertDialog.Builder(activity as Activity)
            .setTitle(if (newNote) "Nova mensagem" else "Editar mensagem")
            .setView(view)
            .setPositiveButton("Salvar", this)
            .setNegativeButton("Cancelar", this)
            .create()
    }


    override fun onClick(dialog: DialogInterface?, which: Int) {
        val values = ContentValues()
        values.put(TITLE_NOTES, noteEditTitle.text.toString())
        values.put(DESCRIPTION_NOTES, noteEditDescription.text.toString())

        if (id != 0L) {
            val uri = Uri.withAppendedPath(URI_NOTES, id.toString())
            context?.contentResolver?.update(uri, values, null, null)
        } else {
            context?.contentResolver?.insert(URI_NOTES, values)
        }
    }
}