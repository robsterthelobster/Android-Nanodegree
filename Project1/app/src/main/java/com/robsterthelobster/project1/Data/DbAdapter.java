package com.robsterthelobster.project1.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DbAdapter extends SQLiteOpenHelper{

    private static final String LOG_TAG = DbAdapter.class.getSimpleName();
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "movies.db";

    static SQLiteDatabase mDB;

    public DbAdapter(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mDB = getWritableDatabase();

        updateMovies();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        //MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_GENRE_IDS + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " +
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

    public ArrayList<String> getPosterPaths(){

        final String BASE = "http://image.tmdb.org/t/p/w185/";

        ArrayList<String> urls = new ArrayList<String>();
        Cursor cursor = getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME, new String[]{MovieContract.MovieEntry.COLUMN_POSTER_PATH}, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String url = BASE + cursor.getString(0);
            urls.add(url);
            cursor.moveToNext();
            //Log.v(LOG_TAG, "movie poster: " + url);
        }

        return urls;
    }
}
