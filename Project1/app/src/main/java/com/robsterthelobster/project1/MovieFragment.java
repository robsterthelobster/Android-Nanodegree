package com.robsterthelobster.project1;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.robsterthelobster.project1.data.MovieContract;

/**
 * Created by robin on 3/24/2016.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private ImageAdapter mImageAdapter;
    private static final int MOVIE_LOADER = 0;
    GridView gv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);

        gv = (GridView) rootView.findViewById(R.id.grid_view);
        mImageAdapter = new ImageAdapter(getActivity(), null, 0);
        gv.setAdapter(mImageAdapter);
        gv.setOnScrollListener(new ScrollListener(getActivity()));
        updateMovies();
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovies(){
        FetchMovieTask task = new FetchMovieTask(getActivity());
        task.execute();
    }

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ADULT,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieContract.MovieEntry.COLUMN_TITLE ,
            MovieContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_COUNT,
            MovieContract.MovieEntry.COLUMN_VIDEO,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    public static int COL_ID = 0;
    public static int COL_POSTER = 1;
    public static int COL_ADULT = 2;
    public static int COL_OVERVIEW = 3;
    public static int COL_RELEASE = 4;
    public static int COL_MOVIE_ID = 5;
    public static int COL_OG_TITLE = 6;
    public static int COL_LANGUAGE = 7;
    public static int COL_TITLE = 8;
    public static int COL_BACKDROP = 9;
    public static int COL_POPULARITY = 10;
    public static int COL_VOTE_COUNT = 11;
    public static int COL_VIDEO = 12;
    public static int COL_VOTE_AVERAGE = 13;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        Uri movieURI = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieURI,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mImageAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mImageAdapter.swapCursor(null);
    }



}
