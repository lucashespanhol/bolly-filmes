package br.com.modulo2.androidaccelerate.projeto_4.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.com.modulo2.androidaccelerate.projeto_4.BuildConfig;
import br.com.modulo2.androidaccelerate.projeto_4.R;
import br.com.modulo2.androidaccelerate.projeto_4.data.FilmesContract;
import br.com.modulo2.androidaccelerate.projeto_4.model.ItemFilme;
import br.com.modulo2.androidaccelerate.projeto_4.model.JsonUtil;

/**
 * Created by Usuario on 23/10/2017.
 */

public class FilmesIntentService extends IntentService {

    public FilmesIntentService() {
        super("FilmesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // https://api.themoviedb.org/3/movie/popular?api_key=8bfa487acfdc5d5aef0c377f6bac85eb&language=pt-BR

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        //No IntentService não temos um context mais o ApplicationContext tem o mesmo significado
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            /*String ordem = preferences.getString(getString(R.string.prefs_ordem_key), "");
            String idioma = preferences.getString(getString(R.string.prefs_idioma_key), "");*/

        String ordem = preferences.getString(getString(R.string.prefs_ordem_key), "popular");
        String idioma = preferences.getString(getString(R.string.prefs_idioma_key), "pt-BR");

        try {
            String stringUrlBase = "https://api.themoviedb.org/3/movie/"+ ordem +"?";
            String apiKey = "api_key";
            String language = "language";

            Uri uriApi = Uri.parse(stringUrlBase).buildUpon()
                    .appendQueryParameter(apiKey, BuildConfig.TMDB_API_KEY)
                    .appendQueryParameter(language, idioma)
                    .build();

            URL url = new URL(uriApi.toString());// converte a URI em uma string e a deixa pronta para virar uma URL HTTP
            urlConnection = (HttpURLConnection) url.openConnection();// abre a conexão com a API
            urlConnection.setRequestMethod("GET");// seta o metodo HTTP que será utilizado na conexão
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            // Cria um objeto InputStream que receberá o objeto inputStream gerado pela URL, ou seja um JSON
           /* if(inputStream == null){// Verifica se o retono não é nulo
                return;
            }*/
            // O BufferedReader irá ler o objeto inputStream que foi retornado da conexão
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String linha;// Variável auxíliar para receber cada linha do reader
            StringBuilder builder = new StringBuilder();// construtor de strings concatenadas

            while((linha = reader.readLine()) != null){
                // Enquando o reader for diferente de null ele adicina a linha onde ele está na variavel linha
                builder.append(linha);
                builder.append("\n");
            }
            //Como não temos retorno neste método guardamos o Json convertido em um List
            List<ItemFilme> itemFilmes = JsonUtil.fromJsonToList(builder.toString());
            // converto o builder para o List

            if(itemFilmes == null){
                return;
            }

            for (ItemFilme itemFilme : itemFilmes) {
                ContentValues values = new ContentValues();
                values.put(FilmesContract.FilmesEntry._ID, itemFilme.getId());
                values.put(FilmesContract.FilmesEntry.COLUMN_TITULO, itemFilme.getTitulo());
                values.put(FilmesContract.FilmesEntry.COLUMN_DESCRICAO, itemFilme.getDescricao());
                values.put(FilmesContract.FilmesEntry.COLUMN_POSTER_PATH, itemFilme.getPosterPath());
                values.put(FilmesContract.FilmesEntry.COLUMN_CAPA_PATH, itemFilme.getCapaPath());
                values.put(FilmesContract.FilmesEntry.COLUMN_AVALIACAO, itemFilme.getAvaliacao());
                values.put(FilmesContract.FilmesEntry.COLUMN_DATA_LANCAMENTO, itemFilme.getDataLancamento());
                values.put(FilmesContract.FilmesEntry.COLUMN_POPULARIDADE, itemFilme.getPopularidade());

                //String where = FilmesContract.FilmesEntry._ID+ "=?";
                //String[] whereValues = new String[]{String.valueOf(itemFilme.getId())};
                //Update direto utilizando comandos via SQLite puro
                //int update = writableDatabase.update(FilmesContract.FilmesEntry.TABLE_NAME, values, where, whereValues);
                //Update utilizando o nosso ContentProvider através do ContentResolver
                int update = getContentResolver().update(FilmesContract.FilmesEntry.buildForFilmes(itemFilme.getId()), values, null, null);
                //No IntentService, o ContentResolver é acessado diretamente sem o auxilio de um getContext()

                if(update == 0){
                    //Insert direto utilizando comandos via SQLite
                    //writableDatabase.insert(FilmesContract.FilmesEntry.TABLE_NAME, null, values);
                    //Insert utilizando o noso ContentProvider através do ContentResolver
                    getContentResolver().insert(FilmesContract.FilmesEntry.CONTENT_URI, values);
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class FilmesReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentService = new Intent(context, FilmesIntentService.class);
            //cria uma intente comum, utilizando o contexto que vem no chamado do construtor e a classe qeu irá sincronizar os filmes
            context.startService(intentService);
            //Chama o service novamente utilizando o contexto passado por parametro
        }
    }

}
