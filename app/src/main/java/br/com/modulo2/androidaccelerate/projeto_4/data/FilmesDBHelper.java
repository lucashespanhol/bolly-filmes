package br.com.modulo2.androidaccelerate.projeto_4.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JoséLucas on 22/08/2017.
 */

public class FilmesDBHelper extends SQLiteOpenHelper {

    private static int DATABASE_VERSION = 3;
    private static String DATABASE_NAME = "bollyFilmes";

    public FilmesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// este método é criado toda vez que o aplicativo é instalado.
        String sqlTableFilmes = "CREATE TABLE " +FilmesContract.FilmesEntry.TABLE_NAME+ " ("
                +FilmesContract.FilmesEntry._ID+ " INTEGER PRIMARY KEY, "
                +FilmesContract.FilmesEntry.COLUMN_TITULO+ " TEXT NOT NULL, "
                +FilmesContract.FilmesEntry.COLUMN_DESCRICAO+ " TEXT NOT NULL, "
                +FilmesContract.FilmesEntry.COLUMN_POSTER_PATH+ " TEXT NOT NULL, "
                +FilmesContract.FilmesEntry.COLUMN_CAPA_PATH+ " TEXT NOT NULL, "
                +FilmesContract.FilmesEntry.COLUMN_AVALIACAO+ " REAL, "
                +FilmesContract.FilmesEntry.COLUMN_DATA_LANCAMENTO+ " TEXT NOT NULL, "
                +FilmesContract.FilmesEntry.COLUMN_POPULARIDADE+ " REAL "
                +" ); ";

        db.execSQL(sqlTableFilmes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {// método executado quando altera a versão do banco
        db.execSQL("DROP TABLE " +FilmesContract.FilmesEntry.TABLE_NAME);

        onCreate(db);
    }
}
