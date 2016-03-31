package com.robsterthelobster.project2;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.robsterthelobster.project2.data.MovieContract;

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

/**
 * Created by robin on 3/24/2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<String>> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context){
        mContext = context;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {

        // no sorting method
        if(params.length == 0){
            return null;
        }

        ArrayList<String> paths = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = "";

        // populate database
        try {

            final String MOVIE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";
            String sort = params[0];

            Uri builtUri = Uri.parse(MOVIE_URL).buildUpon()
                    .appendPath(sort) // need to pull from prefs
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

    private ArrayList<String> parseMovieJson(String movieJsonStr) {

        final String MDB_LIST = "results";

        ArrayList<String> paths = new ArrayList<String>();

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);
            String boolToStr;

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String poster_path = movie.getString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

                boolToStr = movie.getString(MovieContract.MovieEntry.COLUMN_ADULT);
                int adult = (boolToStr == "true") ? 1 : 0;

                String overview = movie.getString(MovieContract.MovieEntry.COLUMN_OVERVIEW);
                String date = movie.getString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                int id = movie.getInt(MovieContract.MovieEntry.COLUMN_ID);
                String og_title = movie.getString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
                String language = movie.getString(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
                String  title = movie.getString(MovieContract.MovieEntry.COLUMN_TITLE);
                String backdrop = movie.getString(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
                double popularity = movie.getDouble(MovieContract.MovieEntry.COLUMN_POPULARITY);
                int vote = movie.getInt(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

                boolToStr = movie.getString(MovieContract.MovieEntry.COLUMN_VIDEO);
                int video = (boolToStr == "true") ? 1 : 0;

                double average = movie.getDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ADULT, adult);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, og_title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, language);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdrop);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, vote);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, video);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, average);

                mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return paths;
    }
}