package br.com.modulo2.androidaccelerate.projeto_4.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JoséLucas on 22/08/2017.
 */

public final class FilmesContract {

    // 1 - cria o content authority para definir de qual app é o content privider
    public static final String CONTENT_AUTHORITY = "br.com.modulo2.androidaccelerate.projeto_4";
    // 2 - cria uma URI base para chamadas dentro do content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +CONTENT_AUTHORITY);
    //3 - Cria o path ou seja, o que eu quero buscar
    public static final String PATH_FILMES = "filmes";

    private FilmesContract(){}

    public static abstract class FilmesEntry implements BaseColumns{

        // 4 - Cria uma URI juntando a URI_BASE com o path que estaremos utilizando
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILMES).build();
        // 5 - Define o Type e o Type_item, para sinalizar o cursor do retorno da URI.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+ "/" +CONTENT_AUTHORITY+ "/" +PATH_FILMES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+ "/" +CONTENT_AUTHORITY+ "/" +PATH_FILMES;

        public static final String TABLE_NAME = "filmes";

        public static final String _ID = "_id";
        public static final String COLUMN_TITULO = "titulo";
        public static final String COLUMN_DESCRICAO = "descricao";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_CAPA_PATH = "capaPath";
        public static final String COLUMN_AVALIACAO = "avaliacao";
        public static final String COLUMN_DATA_LANCAMENTO = "dataLancamento";
        public static final String COLUMN_POPULARIDADE = "popularidade";

        // 6 - cria um métudo para retornar a URI sem parâmetro, ou seja, sem o id
        public static Uri buildForFilmes(){
            return CONTENT_URI.buildUpon().build();// simplesmente mando construir a URI
        }

        // 7 - cria um métudo para retornar a URI com parâmetro, ou seja, com o id
        public static Uri buildForFilmes(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id); // Utiliza o recurso da URI para criar uma URI contatenada com o id
        }

        // 8 - Cria um método para devolver o id de uma URL
        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1)); // o método getPathSegments quebra a uri em várias partes utilizndo o "/" a posição 1 é a do id
        }
    }
}
