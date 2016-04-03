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
import android.widget.Toast;

import com.robsterthelobster.project2.models.Review;
import com.robsterthelobster.project2.models.ReviewModel;
import com.robsterthelobster.project2.models.Trailer;
import com.robsterthelobster.project2.models.TrailerModel;
import com.robsterthelobster.project2.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Call;
import retrofit2.Callback;
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

    private static final int DETAIL_LOADER = 0;
    private Uri mUri;

    static final String API_URL = "https://api.themoviedb.org/3/movie/";

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

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(new MovieAPIInterceptor());
        OkHttpClient client = builder.build();


        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        MovieDBService service = retrofit.create(MovieDBService.class);

        Call<ReviewModel> call = service.listReviews(157336);

        call.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                System.out.println("Success");
                if(response.body() != null){
                    System.out.println("not null");
                    for(Review trailer : response.body().getResults()){
                        System.out.println(trailer.getUrl());
                        System.out.println(response.body().toString());
                    }
                }else{
                    System.out.println("null");
                }
            }

            @Override
            public void onFailure(Call<ReviewModel> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to retrieve movie data", Toast.LENGTH_SHORT).show();
            }
        });
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
                    .error(R.drawable.no_poster_w185) //
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
