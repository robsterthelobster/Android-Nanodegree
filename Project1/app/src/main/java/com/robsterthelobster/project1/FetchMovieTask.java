/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.robsterthelobster.project1;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.robsterthelobster.project1.data.MovieContract;

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
import java.util.Vector;

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

            Log.v(LOG_TAG, "URL: " + url.toString());

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
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                String poster_path = movie.getString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

                Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return paths;
    }
}