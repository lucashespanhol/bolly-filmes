package br.com.modulo2.androidaccelerate.projeto_4.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

    private ImageView imageView;

    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }



    @Override
    protected Bitmap doInBackground(String... params) {

        String urlImage = params[0];//Pego a String da url de conexão
        Bitmap bitmap = null; // Este tipo de objeto serve para trabalhar com imagens


        try{
            InputStream in = new URL(urlImage).openStream();// abro a url e pego o InputStream dela
            bitmap = BitmapFactory.decodeStream(in);// Este método cria uma imagem

        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap b) {
        try{
            imageView.setImageBitmap(b);

        }catch (Exception e){
            Log.d("DownloadImageTask","Erro",e);
        }


    }
}
