package br.com.modulo2.androidaccelerate.projeto_4.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.com.modulo2.androidaccelerate.projeto_4.BuildConfig;
import br.com.modulo2.androidaccelerate.projeto_4.FilmeDetalheActivity;
import br.com.modulo2.androidaccelerate.projeto_4.R;
import br.com.modulo2.androidaccelerate.projeto_4.data.FilmesContract;
import br.com.modulo2.androidaccelerate.projeto_4.model.ItemFilme;
import br.com.modulo2.androidaccelerate.projeto_4.model.JsonUtil;

/**
 * Created by Usuario on 17/11/2017.
 */

public class FilmesSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 720;// Tempo em segundos 60 segundos * 720 minutos em 12 horas, este é o tempo máximo entre as sincronizações
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3; //Tempo mínimo de espera para sincronizar, caso o SyncManager precise rodar mais vezes
    public static final int NOTIFICACAO_FILME_ID = 100;

    public FilmesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // https://api.themoviedb.org/3/movie/popular?api_key=8bfa487acfdc5d5aef0c377f6bac85eb&language=pt-BR

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        //No IntentService não temos um context mais o ApplicationContext tem o mesmo significado
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            /*String ordem = preferences.getString(getString(R.string.prefs_ordem_key), "");
            String idioma = preferences.getString(getString(R.string.prefs_idioma_key), "");*/

        String ordem = preferences.getString(getContext().getString(R.string.prefs_ordem_key), "popular");
        String idioma = preferences.getString(getContext().getString(R.string.prefs_idioma_key), "pt-BR");

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
                int update = getContext().getContentResolver().update(FilmesContract.FilmesEntry.buildForFilmes(itemFilme.getId()), values, null, null);
                //No IntentService, o ContentResolver é acessado diretamente sem o auxilio de um getContext()

                if(update == 0){
                    //Insert direto utilizando comandos via SQLite
                    //writableDatabase.insert(FilmesContract.FilmesEntry.TABLE_NAME, null, values);
                    //Insert utilizando o noso ContentProvider através do ContentResolver
                    getContext().getContentResolver().insert(FilmesContract.FilmesEntry.CONTENT_URI, values);
                    //Chama o método de notificação
                    notify(itemFilme);
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

    public void notify(ItemFilme itemFilme){
        //Cria um sharedPreference
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        //Pega a chave da notificação e o default
        String notifyFilmesKey = getContext().getString(R.string.prefs_notif_filmes_key);
        String notifyDefault = getContext().getString(R.string.prefs_notif_filmes_default);
        //Pega o valor returnado do sharedPreferences
        boolean notfyPrefs = preferences.getBoolean(notifyFilmesKey, Boolean.parseBoolean(notifyDefault));

        if(!notfyPrefs){
            return;
        }

        // 1 - Cria o Builder de notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(itemFilme.getTitulo())
                .setContentText(itemFilme.getDescricao());

        //2 - Cria um intent e passa os dados do novo filme para ser exibido na FilmeDetalheActivity
        Intent intent = new Intent(getContext(), FilmeDetalheActivity.class);
        Uri uri = FilmesContract.FilmesEntry.buildForFilmes(itemFilme.getId());// Utiliza a FilmeEntry para pegar os dados no novo filme
        intent.setData(uri);// seta os dados vindos atraves da uri na intent

        // 3 - Cria o TaskStackBuilder para podermos ter uma "falsa pilha", assim se o usuário clicar em voltar ele volta na tela principal do celular.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntent(intent);//Adiciona a nossa intent na pilha

        // 4 - Cria uma PendingIntent para que o Framework consiga chamar a nossa aplicação direto
        PendingIntent pending = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);

        // 5 - Cria um NotificationManager para ele colocar as notificações no topo da tela do celular do usuário
        NotificationManager notification = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notification.notify(NOTIFICACAO_FILME_ID, builder.build());
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int syncFlexTime){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        //Verifica a versão do android, se for >= a kitkat, ele irá utilizar um recurso disponível a partir dessa versão
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            SyncRequest syncRequest = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, syncFlexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(syncRequest);

        }else{
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);// Nas versões anteriores ao kitkat, não temos a opção flex
        }
    }

    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);// Flag utilizada para sincronizar imediatamente
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);// Flag para indicar que permite disparo manual
        // Indica para o ContentResolver qual a conta utilizada para fazer a sincronia
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if(accountManager.getPassword(account) == null){
            if(!accountManager.addAccountExplicitly(account, "", null)){
                return  null;
            }
            onAccountCreated(account, context);
        }
        return account;
    }

    private static void onAccountCreated(Account account, Context context){
        FilmesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }
}
