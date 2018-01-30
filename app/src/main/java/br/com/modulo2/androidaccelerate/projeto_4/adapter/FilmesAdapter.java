package br.com.modulo2.androidaccelerate.projeto_4.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.modulo2.androidaccelerate.projeto_4.R;
import br.com.modulo2.androidaccelerate.projeto_4.data.FilmesContract;
import br.com.modulo2.androidaccelerate.projeto_4.model.DownloadImageTask;
import br.com.modulo2.androidaccelerate.projeto_4.model.ItemFilme;

/**
 * Created by JoséLucas on 21/04/2017.
 */

public class FilmesAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_DESTAQUE = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private boolean useFilmeDestaque = false;

    public FilmesAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);// O 0(zero) é uma flag que indica o comportamento do cursor, 0(zero) indica o comportamento padão
    }

    public static class ItemFilmeHolder{
        TextView titulo;
        TextView desc;
        TextView dataLancamento;
        RatingBar avaliacao;
        ImageView poster;
        ImageView capa;

        public ItemFilmeHolder(View itemView){
            titulo = (TextView) itemView.findViewById(R.id.item_titulo);
            desc = (TextView) itemView.findViewById(R.id.item_desc);
            dataLancamento = (TextView) itemView.findViewById(R.id.item_data);
            avaliacao = (RatingBar) itemView.findViewById(R.id.item_avaliacao);
            poster = (ImageView) itemView.findViewById(R.id.item_poster);
            capa = (ImageView) itemView.findViewById(R.id.item_capa);
        }
    }

   /* @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int viewType = getItemViewType(position);
        ItemFilme filme = getItem(position);
        View itemView = convertView;

        switch (viewType){
            case VIEW_TYPE_DESTAQUE:{
                itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_filme_destaque, parent, false);

                TextView titulo = (TextView) itemView.findViewById(R.id.item_titulo);
                titulo.setText(filme.getTitulo());

                RatingBar avaliacao = (RatingBar) itemView.findViewById(R.id.item_avaliacao);
                avaliacao.setRating(filme.getAvaliacao());

                ImageView capa = (ImageView) itemView.findViewById(R.id.item_capa);
                new DownloadImageTask(capa).execute(filme.getCapaPath());

                break;
            }case VIEW_TYPE_ITEM: {
                itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_filme, parent, false);

                ItemFilmeHolder holder;
                if(itemView.getTag() == null){
                    holder = new ItemFilmeHolder(itemView);
                    itemView.setTag(holder);
                }else{
                    holder = (ItemFilmeHolder) itemView.getTag();
                }

                holder.titulo.setText(filme.getTitulo());
                holder.desc.setText(filme.getDescricao());
                holder.dataLancamento.setText(filme.getDataLancamento());
                holder.avaliacao.setRating(filme.getAvaliacao());

                new DownloadImageTask(holder.poster).execute(filme.getPosterPath());

                break;
            }
        }

        return itemView;
    }*/
    // Os métodos newView e bindView cada um deles implementa uma parte do método getView
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {// Serve para montar e identificar as views, por exemplo se é uma view de destaque ou não
        int viewType = getItemViewType(cursor.getPosition());// recupera a posição do item dentro do cursor
        int layoutId = -1;

        switch (viewType){//verifica qual layout será carregado.
            case VIEW_TYPE_DESTAQUE:
                layoutId = R.layout.item_filme_destaque;
                break;
            case VIEW_TYPE_ITEM:
                layoutId = R.layout.item_filme;
                break;
        }

        View view = LayoutInflater.from(context).inflate(layoutId,parent,false);// Cria uma nova view e infla a view com o layout definido
        ItemFilmeHolder holder = new ItemFilmeHolder(view);// Utiliza o método holder para preencher a view
        view.setTag(holder);//seta o holder na view

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {//Responsável por pegar os valores e colocar dentro da view

        ItemFilmeHolder holder = (ItemFilmeHolder) view.getTag();//Recupera o holder
        int viewType = getItemViewType(cursor.getPosition());// recupera a posição do item dentro do cursor
        //Preciso pegar os index das colunas na tabela, para isso será utilizado o CONTRACT
        int tituloIndex = cursor.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_TITULO);
        int descricaoIndex = cursor.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_DESCRICAO);
        int posterIndex = cursor.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_POSTER_PATH);
        int capaIndex = cursor.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_CAPA_PATH);
        int avaliacaoIndex = cursor.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_AVALIACAO);
        int dataIndex = cursor.getColumnIndex(FilmesContract.FilmesEntry.COLUMN_DATA_LANCAMENTO);

        switch (viewType){//verifica quais dados serão carregados
            case VIEW_TYPE_DESTAQUE: {
                holder.titulo.setText(cursor.getString(tituloIndex));
                holder.avaliacao.setRating(cursor.getFloat(avaliacaoIndex));

                new DownloadImageTask(holder.capa).execute(cursor.getString(capaIndex));
                break;
            }
            case VIEW_TYPE_ITEM: {
                holder.titulo.setText(cursor.getString(tituloIndex));
                holder.desc.setText(cursor.getString(descricaoIndex));
                holder.dataLancamento.setText(cursor.getString(dataIndex));
                holder.avaliacao.setRating(cursor.getFloat(avaliacaoIndex));

                new DownloadImageTask(holder.poster).execute(cursor.getString(posterIndex));
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && useFilmeDestaque){
            return VIEW_TYPE_DESTAQUE;
        }else{
            return VIEW_TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void setUseFilmeDestaque(boolean useFilmeDestaque) {
        this.useFilmeDestaque = useFilmeDestaque;
    }
}
