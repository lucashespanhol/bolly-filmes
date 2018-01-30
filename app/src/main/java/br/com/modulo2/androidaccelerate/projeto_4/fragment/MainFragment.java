package br.com.modulo2.androidaccelerate.projeto_4.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.com.modulo2.androidaccelerate.projeto_4.BuildConfig;
import br.com.modulo2.androidaccelerate.projeto_4.R;
import br.com.modulo2.androidaccelerate.projeto_4.adapter.FilmesAdapter;
import br.com.modulo2.androidaccelerate.projeto_4.data.FilmesContract;
import br.com.modulo2.androidaccelerate.projeto_4.data.FilmesDBHelper;
import br.com.modulo2.androidaccelerate.projeto_4.model.ItemFilme;
import br.com.modulo2.androidaccelerate.projeto_4.model.JsonUtil;
import br.com.modulo2.androidaccelerate.projeto_4.service.FilmesIntentService;
import br.com.modulo2.androidaccelerate.projeto_4.settings.SettingsActivity;
import br.com.modulo2.androidaccelerate.projeto_4.sync.FilmesSyncAdapter;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int positionItem = ListView.INVALID_POSITION;
    private static final String KEY_POSICAO = "SELECIONADO";
    private ListView lista;
    private boolean useFilmeDestaque = false;
    private FilmesAdapter adapter;
    private static final int FILMES_LOADER = 0;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        lista = (ListView) view.findViewById(R.id.list_filmes);
       // final ArrayList<ItemFilme> filmes = new ArrayList<ItemFilme>();

        //adapter = new FilmesAdapter(getContext(),filmes);
        adapter = new FilmesAdapter(getContext(),null);
        adapter.setUseFilmeDestaque(useFilmeDestaque);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              // ItemFilme iFilme = filmes.get(position);
                Uri uri = FilmesContract.FilmesEntry.buildForFilmes(id);
                Callback callback = (Callback) getActivity();
                callback.onItemSelected(uri);

                positionItem = position;
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_POSICAO)){
            positionItem = savedInstanceState.getInt(KEY_POSICAO);
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(getString(R.string.pd_carregando_title));
        progressDialog.setMessage(getString(R.string.pd_carregando_message));
        progressDialog.setCancelable(false);

        getLoaderManager().initLoader(FILMES_LOADER, null, this);

        // new FilmesAsyncTask().execute();
        //Intent intent = new Intent(getContext(), FilmesIntentService.class);
        //getActivity().startService(intent);

        return view;
    }

    @Override
    public void onResume() {// Iremos utilizar este método quando o usuário alterar alguma preferencia e voltar na tela, assim iremos carregar o Loader novamente
        super.onResume();
        getLoaderManager().restartLoader(FILMES_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        progressDialog.show();

        //Cria um array de Strings indicando quais compos queremos trabalhar
        String[] projection = {
                FilmesContract.FilmesEntry._ID,
                FilmesContract.FilmesEntry.COLUMN_TITULO,
                FilmesContract.FilmesEntry.COLUMN_DESCRICAO,
                FilmesContract.FilmesEntry.COLUMN_POSTER_PATH,
                FilmesContract.FilmesEntry.COLUMN_CAPA_PATH,
                FilmesContract.FilmesEntry.COLUMN_AVALIACAO,
                FilmesContract.FilmesEntry.COLUMN_DATA_LANCAMENTO,
                FilmesContract.FilmesEntry.COLUMN_POPULARIDADE
        };

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String ordem = preferences.getString(getString(R.string.prefs_ordem_key), "popular");// Recupera a ordem selecionada pelo usuário
        String popularValue = getResources().getStringArray(R.array.prefs_ordem_values)[0];// recupera a primeira opção que é popular

        String orderBy = null;
        if(ordem.equals(popularValue)){
            orderBy = FilmesContract.FilmesEntry.COLUMN_POPULARIDADE+ " DESC";
        }else{
            orderBy = FilmesContract.FilmesEntry.COLUMN_AVALIACAO+ " DESC";
        }

        return new CursorLoader(getContext(), FilmesContract.FilmesEntry.CONTENT_URI,projection,null,null,orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);//Carrega o adapter com o cursor carregado acima
        progressDialog.dismiss();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);//Zera os dados no adapter
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(positionItem != ListView.INVALID_POSITION){
            outState.putInt(KEY_POSICAO, positionItem);
        }
        super.onSaveInstanceState(outState);
    }

    /*@Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(positionItem != ListView.INVALID_POSITION && lista != null){
            lista.smoothScrollToPosition(positionItem);
        }
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            lista.smoothScrollToPosition(savedInstanceState.getInt(KEY_POSICAO));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_atualizar:
               // new FilmesAsyncTask().execute();

                //1- Cria uma intent normal passando um contexto e a classe que será chamada
               // Intent intentAlarme = new Intent(getContext(), FilmesIntentService.FilmesReceiver.class);
                //2- Encapsular a Intente em uma Peding intent
               // PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intentAlarme, PendingIntent.FLAG_ONE_SHOT);
                // O zero (0) indica que não queremos saber o codigo de que fez a requisição. A FLAG_ONE_SHOT indica que será executado uma unica vez
                //3- Criar um AlarmManager
               // AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
               // alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                /**
                 * O AlarmManager.RTC, indica que o disparo não irá tem efeito vizual para o usuário, o segundo parâmetro indica o tempo atual,
                 * o terceiro é o intervalo que será disparado, no caso um dia e por ultimo a pending intent.
                */

                FilmesSyncAdapter.syncImmediately(getContext());

                Toast.makeText(getContext(),"Atualizando filmes", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_config:
                startActivity(new Intent(getContext(), SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /*public class FilmesAsyncTask extends AsyncTask<Void, Void, List<ItemFilme>>{

        @Override
        protected List<ItemFilme> doInBackground(Void... params) {
            // https://api.themoviedb.org/3/movie/popular?api_key=8bfa487acfdc5d5aef0c377f6bac85eb&language=pt-BR

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            //String ordem = preferences.getString(getString(R.string.prefs_ordem_key), "");
            //String idioma = preferences.getString(getString(R.string.prefs_idioma_key), "");

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
                if(inputStream == null){// Verifica se o retono não é nulo
                    return null;
                }
                // O BufferedReader irá ler o objeto inputStream que foi retornado da conexão
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String linha;// Variável auxíliar para receber cada linha do reader
                StringBuilder builder = new StringBuilder();// construtor de strings concatenadas

                while((linha = reader.readLine()) != null){
                    // Enquando o reader for diferente de null ele adicina a linha onde ele está na variavel linha
                    builder.append(linha);
                    builder.append("\n");
                }

                return JsonUtil.fromJsonToList(builder.toString());
                // converto o builder para o List
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
            return null;
        }

        @Override
        protected void onPostExecute(List<ItemFilme> itemFilmes) {

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
                int update = getContext().getContentResolver().update(FilmesContract.FilmesEntry.buildForFilmes(itemFilme.getId()), values, null, null);

                if(update == 0){
                    //Insert direto utilizando comandos via SQLite
                    //writableDatabase.insert(FilmesContract.FilmesEntry.TABLE_NAME, null, values);
                    //Insert utilizando o noso ContentProvider através do ContentResolver
                    getContext().getContentResolver().insert(FilmesContract.FilmesEntry.CONTENT_URI, values);
                }
            }

            //adapter.clear();// limpa o adapter para não pegar sujeira
            //adapter.addAll(itemFilmes);// adiciona o item filme no adapter
            //adapter.notifyDataSetChanged();// notifica a chamada que algo foi modificado
        }
    }*/

    public interface Callback{
        void onItemSelected(Uri uri);
    }

    public void setUseFilmeDestaque(boolean useFilmeDestaque){
        this.useFilmeDestaque = useFilmeDestaque;

        if(adapter != null){
            adapter.setUseFilmeDestaque(useFilmeDestaque);
        }
    }
}
