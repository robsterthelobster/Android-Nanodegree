package com.udacity.gradle.builditbigger.gce;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.robin.myapplication.backend.myApi.MyApi;
import com.example.robin.myapplication.backend.myApi.model.Joke;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.gradlelessons.robsterthelobster.jokedisplay.JokeActivity;
import com.udacity.gradle.builditbigger.MainActivity;
import com.udacity.gradle.builditbigger.MainActivityFragment;

import java.io.IOException;

/**
 * Created by robin on 5/26/2016.
 * from gce endpoints tutorial
 */
public class EndpointsAsyncTask extends AsyncTask<Context, Void, Joke> {
    private static String LOG_TAG = EndpointsAsyncTask.class.getSimpleName();
    private static MyApi myApiService = null;
    private Context context;

    // emulator
    private static String IP = "10.0.2.2";

    @Override
    protected Joke doInBackground(Context... params) {

        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://" + IP + ":8080/_ah/api")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        context = params[0];

        try {
            return myApiService.getJoke().execute();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Joke result) {
        MainActivity.spinner.setVisibility(View.GONE);
        if(result == null){
            Toast.makeText(context, "Could not get joke from the server", Toast.LENGTH_LONG).show();
        }else{
            Intent intent = new Intent(context, JokeActivity.class);
            intent.putExtra(JokeActivity.JOKE_KEY, result.getData());
            context.startActivity(intent);
        }
    }
}