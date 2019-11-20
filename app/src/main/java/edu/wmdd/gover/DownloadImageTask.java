package edu.wmdd.gover;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap bmp = null;
        if (!urldisplay.equals("")) {
            urldisplay = urldisplay.replace("http:", "https:");
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("DownloadImageTask", e.getMessage());
                e.printStackTrace();
            }
        }
        return bmp;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}