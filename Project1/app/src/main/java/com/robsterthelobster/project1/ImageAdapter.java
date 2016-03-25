package com.robsterthelobster.project1;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.robsterthelobster.project1.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class ImageAdapter extends CursorAdapter {
    private final Context context;
    private final List<String> urls = new ArrayList<String>();

    public ImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PosterImageView view = (PosterImageView) convertView;
        if (view == null) {
            view = new PosterImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.dog) //
                .error(R.drawable.dog) //
                .fit() //
                .tag(context) //
                .into(view);

        return view;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        PosterImageView view =  new PosterImageView(context);
        view.setScaleType(CENTER_CROP);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final String BASE = "http://image.tmdb.org/t/p/w185/";
        String path = BASE + cursor.getString(1);

        Log.v("Adapter ", path);

        Picasso.with(context) //
                .load(path) //
                .placeholder(R.drawable.dog) //
                .error(R.drawable.dog) //
                .fit() //
                .tag(context) //
                .into((ImageView) view);

    }

    @Override
    public Cursor swapCursor(Cursor cursor){
        Cursor c = super.swapCursor(cursor);
        addToUrls(cursor);
        return c;
    }

    private void addToUrls(Cursor cursor){
        if(cursor!=null){
            cursor.moveToFirst();
            while(!cursor.isLast()){
                final String BASE = "http://image.tmdb.org/t/p/w185/";
                String path = BASE + cursor.getString(1);
                System.out.println(path);
                urls.add(path);
                cursor.moveToNext();
            }
        }
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

}