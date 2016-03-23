package com.robsterthelobster.project1.Data;

import android.provider.BaseColumns;

/**
 * Created by robin on 3/22/2016.
 */
public class MovieContract {

    // movie table
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        // primary
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
    }

    public static final class GenreEntry implements  BaseColumns{
        public static final String TABLE_NAME = "genres";
        // foreign
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_GENRE = "genre";
    }
}
