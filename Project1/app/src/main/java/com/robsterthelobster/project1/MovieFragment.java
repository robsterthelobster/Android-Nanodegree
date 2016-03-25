package com.robsterthelobster.project1;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        Uri movieURI = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieURI,
                MOVIE_COLUMNS,
                null,
                null,
                null);
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
