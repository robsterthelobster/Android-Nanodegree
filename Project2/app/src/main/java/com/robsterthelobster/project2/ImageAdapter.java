package com.robsterthelobster.project2;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class ImageAdapter extends CursorAdapter {
    private final Context mContext;
    private final List<String> urls = new ArrayList<String>();

    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        ImageView view =  new ImageView(context);
        view.setScaleType(CENTER_CROP);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView v = (ImageView) view;
        if (v == null) {
            v = new ImageView(mContext);
            v.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = urls.get(cursor.getPosition());

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(mContext) //
                .load(url) //
                .placeholder(R.drawable.no_poster_w185) //
                        //.error(R.drawable.dog) //
                .fit() //
                .tag(mContext) //
                .into(v);
    }

    @Override
    public Cursor swapCursor(Cursor cursor){
        addToUrls(cursor);
        return super.swapCursor(cursor);
    }

    private void addToUrls(Cursor cursor){
        urls.clear();
        if(cursor!=null){
            while (cursor.moveToNext()){
                final String BASE = "http://image.tmdb.org/t/p/w185/";
                String path = BASE + cursor.getString(MovieFragment.COL_POSTER);

                urls.add(path);
            }
        }
    }
}