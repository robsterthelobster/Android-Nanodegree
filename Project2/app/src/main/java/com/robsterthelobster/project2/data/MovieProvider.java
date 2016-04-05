package com.robsterthelobster.project2.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by robin on 3/24/2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;
    // number of movies, as provided by the API
    // can be reduced to any even number to maintain grid
    private static final String numOfMovies = "20";
    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int MOVIE_FAVORITE = 102;
    static final int FAVORITE = 200;

    private static final SQLiteQueryBuilder movieFavoriteQueryBuilder;

    static{
        movieFavoriteQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        movieFavoriteQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoriteEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_ID +
                        " = " + MovieContract.FavoriteEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteEntry.COLUMN_MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        numOfMovies
                );
                break;
            }
            case MOVIE_WITH_ID:
            {
                return getMovieWithID(uri, projection, sortOrder);
            }
            case FAVORITE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_FAVORITE:
            {
                retCursor = mOpenHelper.getReadableDatabase().rawQuery(
                        "SELECT movies._id, poster_path, id FROM movies, favorites " +
                        "WHERE movies.id = favorites.movie_id AND favorites.favorite = 1", null
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMovieWithID(Uri uri, String[] projection, String sortOrder){

        String selection = MovieContract.MovieEntry.COLUMN_ID + "=?";
        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getIdFromUri(uri)};

        Cursor retCursor = mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                // replace movie data, mainly popularity and votes
                long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE: {
                long _id = db.insertWithOnConflict(MovieContract.FavoriteEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if ( _id > 0 )
                    returnUri = MovieContract.FavoriteEntry.buildFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE:
                rowsDeleted = db.delete(
                        MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/favorites", MOVIE_FAVORITE);

        return matcher;
    }
}
