package com.robsterthelobster.project1.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.robsterthelobster.project1.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DbAdapter extends SQLiteOpenHelper{

    private static final String LOG_TAG = DbAdapter.class.getSimpleName();
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "movies.db";

    SQLiteDatabase mDB;

    public DbAdapter(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mDB = getWritableDatabase();

        updateMovies();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_GENRE_IDS + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    public void updateMovies(){
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    public Cursor fetch(){
        return getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME, new String[]{MovieContract.MovieEntry.COLUMN_TITLE}, null, null, null, null, null);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
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
                parseMovieJson(movieJsonStr);

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
            return null;
        }

        private void parseMovieJson(String movieJsonStr){

            final String MDB_LIST = "results";

            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                JSONArray movieArray = movieJson.getJSONArray(MDB_LIST);

                for (int i = 0; i < movieArray.length(); i++) {

                    String poster_path;
                    int adult;
                    String overview;
                    String date;
                    String genre;
                    int id;
                    String og_title;
                    String language;
                    String title;
                    String backdrop;
                    double popularity;
                    int vote;
                    int video;
                    double average;

                    JSONObject movie = movieArray.getJSONObject(i);

                    String boolToStr = "";

                    poster_path = movie.getString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

                    boolToStr = movie.getString(MovieContract.MovieEntry.COLUMN_ADULT);
                    adult = (boolToStr == "true") ? 1 : 0;

                    overview = movie.getString(MovieContract.MovieEntry.COLUMN_OVERVIEW);
                    date = movie.getString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                    genre = movie.getString(MovieContract.MovieEntry.COLUMN_GENRE_IDS);
                    id = movie.getInt(MovieContract.MovieEntry.COLUMN_ID);
                    og_title = movie.getString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
                    language = movie.getString(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
                    title = movie.getString(MovieContract.MovieEntry.COLUMN_TITLE);
                    backdrop = movie.getString(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);
                    popularity = movie.getInt(MovieContract.MovieEntry.COLUMN_ID);
                    vote = movie.getInt(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

                    boolToStr = movie.getString(MovieContract.MovieEntry.COLUMN_VIDEO);
                    video = (boolToStr == "true") ? 1 : 0;

                    average = movie.getDouble(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
                    cv.put(MovieContract.MovieEntry.COLUMN_ADULT, adult);
                    cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                    cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, date);
                    cv.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS, genre);
                    cv.put(MovieContract.MovieEntry.COLUMN_ID, id);
                    cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, og_title);
                    cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, language);
                    cv.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                    cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdrop);
                    cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                    cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, vote);
                    cv.put(MovieContract.MovieEntry.COLUMN_VIDEO, video);
                    cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, average);

                    mDB.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
                }
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
}
