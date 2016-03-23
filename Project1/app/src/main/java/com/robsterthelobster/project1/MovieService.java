package com.robsterthelobster.project1;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by robin on 3/22/2016.
 */
public class MovieService extends IntentService {

    private final String LOG_TAG = MovieService.class.getSimpleName();

    public MovieService() {
        super("Sunshine");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try {
            final String MOVIE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(MOVIE_URL).buildUpon()
                    .appendPath("popular")
                    .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
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
                return;
            }
            movieJsonStr = buffer.toString();
            parseMovieJSON(movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
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
        return;
    }

    /**
     * poster, adult, overview, release, genre, id, OGtitle,
     * lanugage, title, backgdrop, pop, vote, video, average
     */
    private String[] parseMovieJSON(String movieJsonStr) throws JSONException{
        final String MDB_LIST = "results";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_ADULT = "adult";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_GENRE_IDS = "genre_ids";
        final String MDB_ID = "id";
        final String MDB_ORIGINAL_TITLE = "original_title";
        final String MDB_ORIGINAL_LANGUAGE = "original_language";
        final String MDB_TITLE = "title";
        final String MDB_BACKDROP_PATH = "backdrop_path";
        final String MDB_POPULARITY = "popularity";
        final String MDB_VOTE_COUNT = "vote_count";
        final String MDB_VIDEO = "video";
        final String MDB_VOTE_AVERAGE = "vote_average";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
            for (int i = 0; i < movieArray.length(); i++) {


            }
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }
}
