package com.robsterthelobster.project1;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.robsterthelobster.project1.Data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private ImageAdapter mImageAdapter;
    GridView gv;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FetchMovieTask task = new FetchMovieTask();
        task.execute();

        gv = (GridView) findViewById(R.id.grid_view);
        mImageAdapter = new ImageAdapter(this);
        
        gv.setOnScrollListener(new ScrollListener(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class FetchMovieTask extends AsyncTask<String, Void, ArrayList<String>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> paths = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = "";

            // populate database
            try {

                final String MOVIE_URL = "http://api.themoviedb.org/3/movie/";
                final String KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_URL).buildUpon()
                        .appendPath("popular") // need to pull from prefs
                        .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                //Log.v(LOG_TAG, "URL: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieJsonStr = buffer.toString();
                paths = parseMovieJson(movieJsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
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
            return paths;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if(result != null) {
                mImageAdapter.clear();
                mImageAdapter.add(result);
                gv.setAdapter(mImageAdapter);
            }
        }

        private ArrayList<String> parseMovieJson(String movieJsonStr) {

            final String MDB_LIST = "results";
            final String BASE = "http://image.tmdb.org/t/p/w185/";
            ArrayList<String> paths = new ArrayList<String>();

            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);

                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movie = movieArray.getJSONObject(i);
                    String poster_path = movie.getString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
                    paths.add(BASE+poster_path);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return paths;
        }
    }
}
