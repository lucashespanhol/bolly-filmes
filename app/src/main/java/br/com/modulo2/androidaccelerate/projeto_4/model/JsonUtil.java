package br.com.modulo2.androidaccelerate.projeto_4.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Esta classe faz a conversão de um arquivo JSON com vários filmes em um List
public class JsonUtil {

    public static List<ItemFilme> fromJsonToList(String json){//Método que irá converter o aquivo Json em uma lista de Items filme
        List<ItemFilme> lista = new ArrayList<ItemFilme>();
        try {
            JSONObject jsonBase = new JSONObject(json);//O jsonBase é a estrutura do arquivo JSON
            JSONArray results = jsonBase.getJSONArray("results");

            for(int i = 0; i < results.length(); i++){
                JSONObject filmeObject = results.getJSONObject(i);
                ItemFilme itemFilme = new ItemFilme(filmeObject);
                lista.add(itemFilme);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return lista;
    }
}
