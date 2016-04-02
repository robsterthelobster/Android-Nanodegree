package com.robsterthelobster.project2;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robsterthelobster.project2.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by robin on 3/27/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String DETAIL_URI = "URI";
    @Bind(R.id.detail_poster_image) ImageView mPosterView;
    @Bind(R.id.detail_title_text) TextView mTitleView;
    @Bind(R.id.detail_rating_text) TextView mRatingView;
    @Bind(R.id.detail_overview_text) TextView mOverviewView;
    @Bind(R.id.detail_date_text) TextView mReleaseDateView;

    private static final int DETAIL_LOADER = 0;
    private Uri mUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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

        return new CursorLoader(getActivity(),
                mUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            String url = "http://image.tmdb.org/t/p/w185/" + data.getString(COL_POSTER);
            Picasso.with(getContext()) //
                    .load(url) //
                    .placeholder(R.drawable.no_poster_w185) //
                            //.error(R.drawable.dog) //
                    .fit() //
                    .tag(getActivity()) //
                    .into(mPosterView);

            mTitleView.setText(data.getString(COL_OG_TITLE));
            mRatingView.setText(Utility.getRatingStr(data.getString(COL_VOTE_AVERAGE)));
            mOverviewView.setText(data.getString(COL_OVERVIEW));
            mReleaseDateView.setText(Utility.formatDateDMY(data.getString(COL_RELEASE)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {    }

}
