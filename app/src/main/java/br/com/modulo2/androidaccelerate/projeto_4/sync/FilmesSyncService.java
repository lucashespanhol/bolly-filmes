package br.com.modulo2.androidaccelerate.projeto_4.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Usuario on 17/11/2017.
 */

public class FilmesSyncService extends Service {

    private static FilmesSyncAdapter filmesSyncAdapter = null;
    private static final Object lock = new Object();

    @Override
    public void onCreate() {
        synchronized (lock){// esse bloco de código permite que o adapter seja criado apenas uma vez
            if(filmesSyncAdapter == null){
                filmesSyncAdapter = new FilmesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return filmesSyncAdapter.getSyncAdapterBinder();
    }
}
