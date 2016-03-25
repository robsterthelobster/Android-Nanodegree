package com.robsterthelobster.project1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by robin on 3/22/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL " +
//                        MovieContract.MovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
//                        MovieContract.MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " +
//                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL" +
                        " );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}