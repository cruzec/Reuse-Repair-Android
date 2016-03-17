// CS419 - Reuse & Repair Mobile App
// ---------------------------------------
// Charles Jenkins
// <jenkinch@oregonstate.edu>
//
// Billy Kerns
// <kernsbi@oregonstate.edu>
//
// Eric Cruz
// <cruze@oregonstate.edu>
//
// Title: GetMapsImage.java
//
// Description: Get a map image of
// where the business is
// ---------------------------------------
// Acknowledgements:
// http://stackoverflow.com/questions/12088136/android-load-image-from-web-url

package com.example.eric.reuserepair.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GetMapsImage extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String ...urls) {
        String url = urls[0];
        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();

        return loadBitmap(url, bmOptions);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
    }

    public static Bitmap loadBitmap(String URL, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        InputStream in;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            in.close();
        } catch (IOException e1) {
        }
        return bitmap;
    }

    private static InputStream OpenHttpConnection(String strURL)
            throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception ex) {
        }
        return inputStream;
    }
}
