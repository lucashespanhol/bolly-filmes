package br.com.modulo2.androidaccelerate.projeto_4.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Usuario on 25/09/2017.
 */

public class FilmesProvider extends ContentProvider{

    private static UriMatcher URI_MATCHER = buildUriMatcher();
    // Variáveis para as definiçõs das regras do URI_MATCHER
    private static final int FILME = 100;
    private static final int FILME_ID = 101;
    private FilmesDBHelper dbHelper;

    private static UriMatcher buildUriMatcher(){//Método utilizado apenas para inicialização da variável URI_MATECHER
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);// inicialização padrão de um URI_MATCHER
        uriMatcher.addURI(FilmesContract.CONTENT_AUTHORITY, FilmesContract.PATH_FILMES, FILME); // URI_MATCHER para retornar uma lista de filmes
        uriMatcher.addURI(FilmesContract.CONTENT_AUTHORITY, FilmesContract.PATH_FILMES+ "/#", FILME_ID);
        // URI_MATCHER para retornar um único filme, o "/#" indica que estamos passando um id para o ContentProvider.
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FilmesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase readbleDatabase = dbHelper.getReadableDatabase(); // pego uma instacia da tabela
        Cursor cursor;

        switch (URI_MATCHER.match(uri)){// esse metodo retorna a regra que iremos utilizar para fazer as consultas
            case FILME:
                cursor = readbleDatabase.query(FilmesContract.FilmesEntry.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder);
                break;
            case FILME_ID:
                selection = FilmesContract.FilmesEntry._ID+ "=?";
                selectionArgs = new String[]{String.valueOf(FilmesContract.FilmesEntry.getIdFromUri(uri))};
                cursor = readbleDatabase.query(FilmesContract.FilmesEntry.TABLE_NAME, projection, selection, selectionArgs, null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("URI não identificada: " +uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);// Avisa quando há alguma alteração nesta URI.
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)){
            case FILME:
                return FilmesContract.FilmesEntry.CONTENT_TYPE;
            case FILME_ID:
                return FilmesContract.FilmesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("URI não identificada: " +uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        long id;

        switch (URI_MATCHER.match(uri)){
            case FILME:
                id = writableDatabase.insert(FilmesContract.FilmesEntry.TABLE_NAME, null, contentValues);
                if(id == -1){
                    return null;
                }
                break;
            default:
                throw new IllegalArgumentException("URI não identificada: " +uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);// Também indica alteração na Uri, novo insert, o segundo parâmetro não iremos utilizar
        return FilmesContract.FilmesEntry.buildForFilmes(id); // devolve a URI já com o id de inserção
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        int delete = 0;
        switch (URI_MATCHER.match(uri)){
            case FILME:
                delete = writableDatabase.delete(FilmesContract.FilmesEntry.TABLE_NAME, selection, selectionArgs);
            case FILME_ID:
                selection = FilmesContract.FilmesEntry._ID+ "=?";
                selectionArgs = new String[]{String.valueOf(FilmesContract.FilmesEntry.getIdFromUri(uri))};
                delete = writableDatabase.delete(FilmesContract.FilmesEntry.TABLE_NAME, selection, selectionArgs);
        }
        if(delete != 0){
            getContext().getContentResolver().notifyChange(uri, null);// Também indica alteração na Uri, novo delete, o segundo parâmetro não iremos utilizar
        }
        return delete;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        int update = 0;

        switch (URI_MATCHER.match(uri)){
            case FILME:
                update = writableDatabase.update(FilmesContract.FilmesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
            case FILME_ID:
                selection = FilmesContract.FilmesEntry._ID+ "=?";
                selectionArgs = new String[]{String.valueOf(FilmesContract.FilmesEntry.getIdFromUri(uri))};
                update = writableDatabase.update(FilmesContract.FilmesEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        }
        if(update != 0){
            getContext().getContentResolver().notifyChange(uri, null);// Também indica alteração na Uri, novo update, o segundo parâmetro não iremos utilizar
        }

        return update;
    }
}
