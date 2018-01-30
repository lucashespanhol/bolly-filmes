package br.com.modulo2.androidaccelerate.projeto_4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import br.com.modulo2.androidaccelerate.projeto_4.fragment.FilmeDetalheFragment;
import br.com.modulo2.androidaccelerate.projeto_4.model.ItemFilme;

public class FilmeDetalheActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filme_detalhe);

        Intent intent = getIntent();
        //ItemFilme itemFilme = (ItemFilme) intent.getSerializableExtra(MainActivity.FILME_DETALHE_URI);
        Uri uri = intent.getData();

        // Crio a instancia de um objeto tipo fragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        //Crio a instancia de um objeto tipo fragmentTransaction e abro uma transação
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Crio um objeto do tipo FilmeDetalheFragment e posterior um objeto do tipo bundle
        FilmeDetalheFragment filmeDetalheFragment = new FilmeDetalheFragment();
        Bundle bundle = new Bundle();
        //Coloco no bundle uma chave e o objeto do tipo ItemFilme
        //bundle.putSerializable(MainActivity.FILME_DETALHE_URI, itemFilme);
        bundle.putParcelable(MainActivity.FILME_DETALHE_URI, uri);
        //No objeto tipo filmeDetalheFragment podemos passar argumentos do tipo bundle, e passamos o bundle criado
        filmeDetalheFragment.setArguments(bundle);
        //Adiciona o layout através do seu id e passa o fragment que será montado dentro do layout
        fragmentTransaction.add(R.id.fragment_filme_detalhe, filmeDetalheFragment);

        // Finalizo a transação.
        fragmentTransaction.commit();

    }
}
