package com.dmytro.caller;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

//import org.eclipse.jetty.websocket.client.WebSocketClient;


public class GetID extends AsyncTask<Void, Void, String> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Activity activity;

    public GetID(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        //PeerJS API room key
        String roomKey = "adyneiwxyjy8pvi";
        //forming url-string for request.
        String forUrl = "http://0.peerjs.com:9000/" + roomKey + "/id?ts=" +
                Calendar.getInstance().getTimeInMillis() + "." + new Random().nextInt(1000000);

        return sendRequestForResult(forUrl);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);


}

    /**
     * Sending request to PeerJS for getting ID
     *
     * @param forUrl string with url for request
     * @return //todo
     */
    private String sendRequestForResult(String forUrl) {
        URL url = null;
        try {
            url = new URL(forUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            assert url != null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) {
                //something went wrong,
                // todo handle this
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }


            if (buffer.length() == 0) {
                // Stream was empty
                //todo handle this
                return null;
            }
            jsonString = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return jsonString;
    }

}
