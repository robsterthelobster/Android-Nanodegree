package com.robsterthelobster.project2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.robsterthelobster.project2.data.MovieContract;
import com.robsterthelobster.project2.models.Review;
import com.robsterthelobster.project2.models.ReviewModel;
import com.robsterthelobster.project2.models.Trailer;
import com.robsterthelobster.project2.models.TrailerModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
    @Bind(R.id.detail_trailer_list) ListView mTrailerView;
    @Bind(R.id.detail_review_list) ListView mReviewView;
    @Bind(R.id.detail_favorite_check) CheckBox mFavoriteCheck;

    private static final int DETAIL_LOADER = 0;
    private static final int FAVORITE_LOADER = 1;
    private Uri mUri;
    private int movieID;

    static final String API_URL = "https://api.themoviedb.org/3/movie/";
    MovieDBService service;

    public interface MovieDBService {
        @GET("{id}/videos")
        Call<TrailerModel> listTrailers(@Path("id") int id);

        @GET("{id}/reviews")
        Call<ReviewModel> listReviews(@Path("id") int id);
    }

    static class MovieAPIInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            HttpUrl url = chain.request().url().newBuilder().addQueryParameter("api_key", BuildConfig.MOVIEDB_API_KEY).build();
            Request request = chain.request().newBuilder().url(url).build();
            return chain.proceed(request);
        }
    }

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
            movieID = (int) ContentUris.parseId(mUri);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        mFavoriteCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ContentValues data = new ContentValues();
                String where = MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] args = new String[] {String.valueOf(movieID)};

                if(isChecked){
                    System.out.println("checked");
                    data.put(MovieContract.FavoriteEntry.COLUMN_MOVIE_ID, movieID);
                    data.put(MovieContract.FavoriteEntry.COLUMN_FAVORITE, 1);
                    getContext().getContentResolver().insert(
                            MovieContract.FavoriteEntry.CONTENT_URI,
                            data
                    );
                }else{
                    System.out.println("unchecked");
                    data.put(MovieContract.FavoriteEntry.COLUMN_FAVORITE, 0);
                    getContext().getContentResolver().update(
                            MovieContract.FavoriteEntry.CONTENT_URI,
                            data,
                            where,
                            args
                    );
                }
            }
        });

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(new MovieAPIInterceptor());
        OkHttpClient client = builder.build();

        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(MovieDBService.class);

        fetchMovieReviews(movieID);
        fetchMovieTrailers(movieID);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        LoaderManager loader = getLoaderManager();
        loader.initLoader(DETAIL_LOADER, null, this);
        loader.initLoader(FAVORITE_LOADER, null, this);
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
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
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

    final String[] COLUMNS = {
            MovieContract.FavoriteEntry._ID,
            MovieContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MovieContract.FavoriteEntry.COLUMN_FAVORITE
    };
    public static int COL_FAVORITE = 2;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case DETAIL_LOADER:
                return new CursorLoader(getActivity(),
                        mUri,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);
            case FAVORITE_LOADER:
                String selection = MovieContract.FavoriteEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = new String[] {String.valueOf(movieID)};
                return new CursorLoader(getActivity(),
                        MovieContract.FavoriteEntry.CONTENT_URI,
                        COLUMNS,
                        selection,
                        selectionArgs,
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            switch(loader.getId()){
                case DETAIL_LOADER:
                    String url = "http://image.tmdb.org/t/p/w185/" + data.getString(COL_POSTER);
                    Picasso.with(getContext()) //
                            .load(url) //
                            .placeholder(R.drawable.no_poster_w185) //
                            .error(R.drawable.no_poster_w185) //
                            .fit() //
                            .tag(getActivity()) //
                            .into(mPosterView);

                    mTitleView.setText(data.getString(COL_OG_TITLE));
                    mRatingView.setText(Utility.getRatingStr(data.getString(COL_VOTE_AVERAGE)));
                    mOverviewView.setText(data.getString(COL_OVERVIEW));
                    mReleaseDateView.setText(Utility.formatDateDMY(data.getString(COL_RELEASE)));
                    break;
                case FAVORITE_LOADER:
                    if(data.getInt(COL_FAVORITE) == 1){
                        mFavoriteCheck.setChecked(true);
                    }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {    }

    private void fetchMovieReviews(int id){

        Call<ReviewModel> call = service.listReviews(id);

        call.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                if(response.body() != null){
                    List<Review> reviews = response.body().getResults();
                    mReviewView.setAdapter(new ReviewAdapter(getContext(), reviews));
                    Utility.setListViewHeightBasedOnChildren(mReviewView);
                }
            }
            @Override
            public void onFailure(Call<ReviewModel> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to retrieve movie data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovieTrailers(int id){
        Call<TrailerModel> call = service.listTrailers(id);

        call.enqueue(new Callback<TrailerModel>() {
            @Override
            public void onResponse(Call<TrailerModel> call, Response<TrailerModel> response) {
                if(response.body() != null){
                    List<Trailer> trailers = response.body().getTrailers();
                    mTrailerView.setAdapter(new TrailerAdapter(getContext(), trailers));
                    Utility.setListViewHeightBasedOnChildren(mTrailerView);
                }
            }
            @Override
            public void onFailure(Call<TrailerModel> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to retrieve movie data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
