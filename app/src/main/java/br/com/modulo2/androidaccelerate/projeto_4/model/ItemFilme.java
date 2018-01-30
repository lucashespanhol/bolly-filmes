package br.com.modulo2.androidaccelerate.projeto_4.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by JoséLucas on 20/04/2017.
 */

public class ItemFilme implements Serializable{

    private long id;
    private String titulo;
    private String descricao;
    private String dataLancamento;
    private String posterPath;
    private String capaPath;
    private float avaliacao;
    private float popularidade;

    public ItemFilme(long id, String titulo, String descricao, String dataLancamento, String posterPath, String capaPath, float avaliacao, float popularidade) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataLancamento = dataLancamento;
        this.posterPath = posterPath;
        this.capaPath = capaPath;
        this.avaliacao = avaliacao;
        this.popularidade = popularidade;
    }

    public ItemFilme(JSONObject jo) throws JSONException{// este construtor já converte um objeto do tipo JSON e um ItemFilme
        this.id = jo.getInt("id");
        this.titulo = jo.getString("title");
        this.descricao = jo.getString("overview");
        this.dataLancamento = jo.getString("release_date");
        this.posterPath = jo.getString("poster_path");
        this.capaPath = jo.getString("backdrop_path");
        this.avaliacao = (float) jo.getDouble("vote_average");
        this.popularidade = (float) jo.getDouble("popularity");
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDataLancamento() {
        Locale locale = new Locale("pt", "BR");
        try{
            Date data = new SimpleDateFormat("yyyy-MM-dd", locale).parse(dataLancamento);
            return new SimpleDateFormat("dd/MM/yyyy", locale).format(data);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return dataLancamento;
    }

    public void setDataLancamento(String dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getPosterPath() {
        return buildPath("w500", posterPath);
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getCapaPath() {
        return buildPath("w780", capaPath);
    }

    public void setCapaPath(String capaPath) {
        this.capaPath = capaPath;
    }

    public float getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(float avaliacao) {
        this.avaliacao = avaliacao;
    }

    public float getPopularidade() {
        return popularidade;
    }

    public void setPopularidade(float popularidade) {
        this.popularidade = popularidade;
    }

    private String buildPath(String width, String path){
        StringBuilder builder = new StringBuilder();
        builder.append("http://image.tmdb.org/t/p/")
                .append(width)
                .append(path);
        return builder.toString();
    }
}
