package br.com.modulo2.androidaccelerate.projeto_4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import br.com.modulo2.androidaccelerate.projeto_4.fragment.FilmeDetalheFragment;
import br.com.modulo2.androidaccelerate.projeto_4.fragment.MainFragment;
import br.com.modulo2.androidaccelerate.projeto_4.sync.FilmesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    public static final String FILME_DETALHE_URI = "FILME";

    private boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verifica se esse id foi carregado no layout ou não, se for carregado é porque carregou o layout do Tablet
        if(findViewById(R.id.fragment_filme_detalhe) != null){
            //Verifica se não foi criado uma instacia da Activity
            if(savedInstanceState == null){
                //Se não foi carregado, coloca-se o Fragment no layout através do FragmentManager
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_filme_detalhe, new FilmeDetalheFragment())
                        .commit();
            }
            isTablet = true;
        }else{// Senão, ele carregará o layout do celular
            isTablet = false;
        }

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        mainFragment.setUseFilmeDestaque(!isTablet);

        FilmesSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public void onItemSelected(Uri uri) {
        //Comparação para saber se está carregando em um celular ou em um tablete
        if(isTablet){
            //Cria um FragmentManager e um FragmentTransaction para passar dados para um Fragment
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            //Crio um objeto para o tipo de Fragment que estou trabalhando neste caso um FilmeDetalheFragment
            FilmeDetalheFragment detalheFragment = new FilmeDetalheFragment();
            //Crio um Bundle e coloco um serializable como paramentro, neste Caso o ItemFilme que recebemos por parâmetro
            Bundle bundle = new Bundle();
            //bundle.putSerializable(MainActivity.FILME_DETALHE_URI, itemFilme);
            bundle.putParcelable(MainActivity.FILME_DETALHE_URI, uri);
            //Coloco o bundle no setArguments do Fragment criado
            detalheFragment.setArguments(bundle);
            //Subistitui o Fragment criado anteriormente pelo que será carregado a partir do clique
            ft.replace(R.id.fragment_filme_detalhe,detalheFragment);
            ft.commit();
        }else{
            //Para celular crio um intente e passo os valores para a nova activity
            Intent i = new Intent(this, FilmeDetalheActivity.class);
            //i.putExtra(MainActivity.FILME_DETALHE_URI,itemFilme);
            i.setData(uri);
            startActivity(i);
        }
    }
}
