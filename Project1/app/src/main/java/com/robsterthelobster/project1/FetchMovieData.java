package com.robsterthelobster.project1;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by robin on 3/22/2016.
 */
public class FetchMovieData {
    public FetchMovieData(){
        try {
            final String MOVIE_URL = "http://api.themoviedb.org/3/movie/";
            final String KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(MOVIE_URL).buildUpon()
                    .appendPath("popular")
                    .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIEDB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * poster, adult, overview, release, genre, id, OGtitle,
     * lanugage, title, backgdrop, pop, vote, video, average
     */
    private void parseMovieJSON(String movieJsonStr) throws JSONException{
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

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);
    }
}
