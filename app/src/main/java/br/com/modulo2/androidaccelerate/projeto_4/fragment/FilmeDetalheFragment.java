package br.com.modulo2.androidaccelerate.projeto_4.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import br.com.modulo2.androidaccelerate.projeto_4.MainActivity;
import br.com.modulo2.androidaccelerate.projeto_4.R;
import br.com.modulo2.androidaccelerate.projeto_4.data.FilmesContract;
import br.com.modulo2.androidaccelerate.projeto_4.model.DownloadImageTask;

public class FilmeDetalheFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    //private ItemFilme itemFilme;
    private Uri filmeUri;
    private static final int FILME_DETALHE_LOADER = 0;
    //Transforma os atributos viziais em atibutos da classe.
    private TextView tituloView;
    private TextView dataView;
    private TextView descView;
    private RatingBar avaliacaoView;
    private ImageView capaView;
    private ImageView posterView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            filmeUri = (Uri) getArguments().getParcelable(MainActivity.FILME_DETALHE_URI);
        }
        getLoaderManager().initLoader(FILME_DETALHE_LOADER,null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filme_detalhe, container, false);

       // if(itemFilme == null)
           // return view;

        tituloView = (TextView) view.findViewById(R.id.item_titulo);
        //tituloView.setText(itemFilme.getTitulo());

        dataView = (TextView) view.findViewById(R.id.item_data);
        //dataView.setText(itemFilme.getDataLancamento());

        descView = (TextView) view.findViewById(R.id.item_desc);
        //descView.setText(itemFilme.getDescricao());

        avaliacaoView = (RatingBar) view.findViewById(R.id.item_avaliacao);
        //avaliacaoView.setRating(itemFilme.getAvaliacao());

        capaView = (ImageView) view.findViewById(R.id.item_capa);
        //new DownloadImageTask(capaView).execute(itemFilme.getCapaPath());

        //if(view.findViewById(R.id.item_poster) != null){
        posterView = (ImageView) view.findViewById(R.id.item_poster);
            //new DownloadImageTask(posterView).execute(itemFilme.getPosterPath());
       // }

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                FilmesContract.FilmesEntry._ID,
                FilmesContract.FilmesEntry.COLUMN_TITULO,
                FilmesContract.FilmesEntry.COLUMN_DESCRICAO,
                FilmesContract.FilmesEntry.COLUMN_POSTER_PATH,
                FilmesContract.FilmesEntry.COLUMN_CAPA_PATH,
                FilmesContract.FilmesEntry.COLUMN_AVALIACAO,
                FilmesContract.FilmesEntry.COLUMN_DATA_LANCAMENTO
        };
        return new CursorLoader(getContext(), filmeUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null || data.getCount() < 1){
            return ;
        }
        if(data.moveToFirst()){
            int tituloIndex    = data.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_TITULO);
            int descrcaoIndex  = data.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_DESCRICAO);
            int posterIndex    = data.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_POSTER_PATH);
            int capaIndex      = data.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_CAPA_PATH);
            int avaliacaoIndex = data.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_AVALIACAO);
            int dataLancIndex  = data.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_DATA_LANCAMENTO);

            String titulo    = data.getString(tituloIndex);
            String descricao = data.getString(descrcaoIndex);
            String poster    = data.getString(posterIndex);
            String capa      = data.getString(capaIndex);
            String dataLanc  = data.getString(dataLancIndex);
            float avaliacao  = data.getFloat(avaliacaoIndex);

            tituloView.setText(titulo);
            descView.setText(descricao);
            avaliacaoView.setRating(avaliacao);
            dataView.setText(dataLanc);

            new DownloadImageTask(capaView).execute(capa);

            if(posterView != null){
                new DownloadImageTask(posterView).execute(poster);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

