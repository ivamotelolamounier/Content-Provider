/**
 * Função para criar e instânciar todos componetes, variáveis e demais APIS do aplicativo
 * URL de pesquisas
 *("Implement this to initialize your content provider on startup.")
 * Para fazer a validação de um objeto de requisição de uma provider, cria-se uma variavel do
 * tipo 'UriMatch'.
 * O uso /# é para retornar uma 'query String' para requisão de ID
 * Método para deletar dados do provider.
 * ("Implement this to handle requests to delete one or more rows")
 * Primeiro verifica-se se a URI não é nula, se for, trata o erro com a Excepition
 * Sendo válid a URI, entáo habilita o db no modo escrita, após, busca as linhas afetadas
 * através da val 'linesAffect', que é uma array de linhas alteradas
 * Isso feito, notifica-se o context para que seu Resolver carregue a URI alterada no 'observer'
 * o 'notifyChanger', é OBRIGATÒRIO em todas as alterações que ocorrem na ContentProvider do app
 * Como o método 'delete' necessita de um retorno, retorna-se as linhas afetadas
 * Função para validar uma URI, pode ser dados de imagens, arquivos,
 * ("Implement this to handle requests for the MIME type of the data" +
 * "at the given URI")
 *  neste exemplo, não será utilizado o 'getType', uma vez que não haverá manuseio de arquivos ou images
 *  assim, implementa-se um tratamento de exceção para não quebrar o app
 * Função para inserir dados no DB da aplicação através da contentProvider
 * ("Implement this to handle requests to insert a new row.")
 * Função que será o 'SELECT' do contentProvider, com o DB, criar pesquisas, SEMPRE
 * retornará um 'cursor' que é o retorno de dados da varável provider
 * ("Implement this to handle query requests from clients.")
 * FUnção para atualizar as ID das contentProvider
 * ("Implement this to handle requests to update one or more rows.")
 * Em Kotlin, se você deseja escrever uma função ou qualquer membro da classe que
 * pode ser chamado sem ter a instância da classe, você pode escrever o mesmo
 * como um membro de um objeto companheiro dentro da classe.
 * Portanto, ao declarar o objeto complementar , você pode acessar os membros da classe
 * apenas pelo nome da classe (sem criar explicitamente a instância da classe).
 * Na aula de praticidade, o descarregamento quase nunca acontece;
 * portanto , os objetos complementares permanecerão na memória durante
 * o ciclo de vida do aplicativo .
 *  'AUTORITY' Define o endereço do Provider
 * È através da val 'BASE_URI' que se requisita o contentProvider em qualquer aplicação
 * Renomeia o caminho complento da contentProvider
 * URI_NOTES será como: "content://com.ivamotelo.applicationcontentprovider.provider/notes'
 * que é o endereço responsável por acessar todos os dados que estiveram an contentProvider,
 * sendo o método 'withAppendedPath, o concactenador das String, inclusive "/"
 */

package com.ivamotelo.applicationcontentprovider.database

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns._ID
import com.ivamotelo.applicationcontentprovider.database.NotesDataBaseHelper.Companion.TABLE_NOTES

class NotesProvider : ContentProvider() {

    lateinit var mUriMatcher : UriMatcher
    lateinit var dbHelper : NotesDataBaseHelper

    override fun onCreate(): Boolean {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)            // URI vazia
        mUriMatcher.addURI(AUTORITY, "notes", NOTES)        // retona 1
        mUriMatcher.addURI(AUTORITY, "notes/#", NOTES_BY_ID)
        if (context != null) { dbHelper = NotesDataBaseHelper(context as Context) }
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect = db.delete(TABLE_NOTES, "$_ID =?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri,null)
            return linesAffect
        } else {
            throw  UnsupportedSchemeException("URI inválida para exclusão")
        }
    }

    override fun getType(uri: Uri): String = throw UnsupportedSchemeException("Uri não implementada")

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (mUriMatcher.match(uri) == NOTES) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id = db.insert(TABLE_NOTES, null, values)
            val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return insertUri
        } else {
            throw  UnsupportedSchemeException("URI inválida para inserção")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return when {
            mUriMatcher.match(uri) == NOTES -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor =
                    db.query(TABLE_NOTES, projection, selection, selectionArgs, null, null, sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor =
                    db.query(TABLE_NOTES, projection, "$_ID = ?", arrayOf(uri.lastPathSegment),null, null, sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            else -> {
                throw UnsupportedSchemeException("URI não implementada")
            }
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID ) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffect = db.update(TABLE_NOTES, values, "$_ID = ?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        } else {
            throw UnsupportedSchemeException("URI não implementada")
        }
    }

    companion object {

        const val AUTORITY = "com.ivamotelo.applicationcontentprovider.provider"
        val BASE_URI: Uri = Uri.parse("content://$AUTORITY")      // Converte a String passada
        val URI_NOTES: Uri = Uri.withAppendedPath(BASE_URI, "notes")
        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}
