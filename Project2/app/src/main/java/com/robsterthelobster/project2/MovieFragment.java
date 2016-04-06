package com.robsterthelobster.project2;

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
import android.widget.AdapterView;
import android.widget.GridView;

import com.robsterthelobster.project2.data.MovieContract;

/**
 * Created by robin on 3/24/2016.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private ImageAdapter mImageAdapter;
    private static final int MOVIE_LOADER = 0;
    GridView gv;
    private OnItemClickedListener mCallback;

    public interface OnItemClickedListener {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri uri);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        updateMovies();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mImageAdapter = new ImageAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        gv = (GridView) rootView.findViewById(R.id.grid_view);
        gv.setAdapter(mImageAdapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Uri uri = MovieContract.MovieEntry.buildMovieWithID(cursor.getInt(COL_ID));

                try{
                    mCallback = (OnItemClickedListener) getActivity();
                    mCallback.onItemSelected(uri);
                }catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString()
                            + " must implement OnItemClickedListener");
                }
            }
        });
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

    public void onSortingChanged(){
        updateMovies();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void updateMovies(){
        FetchMovieTask task = new FetchMovieTask(getActivity());
        String sort = Utility.getSortType(getActivity());
        if(!sort.equals(getString(R.string.pref_sort_favorite))) {
            task.execute(Utility.getSortType(getActivity()));
        }
    }

    private static final String[] projection = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ID
    };
    // column poster is stored in
    public static final int COL_POSTER = 1;
    public static final int COL_ID = 2;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = "";
        String sort = Utility.getSortType(getActivity());

        Uri movieURI = MovieContract.MovieEntry.CONTENT_URI;
        String selection = MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?" + " AND " + MovieContract.FavoriteEntry.COLUMN_FAVORITE + "=?";
        String[] selectionArgs = new String[] {MovieContract.MovieEntry.COLUMN_ID, "1"};

        if(sort.equals(getString(R.string.pref_sort_popular))){
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        }else if(sort.equals(getString(R.string.pref_sort_top))){
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }else if(sort.equals(getString(R.string.pref_sort_favorite))){
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.buildMovieFavorites(),
                    projection,
                    selection,
                    selectionArgs,
                    null);
        }

        return new CursorLoader(getActivity(),
                movieURI,
                projection,
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
