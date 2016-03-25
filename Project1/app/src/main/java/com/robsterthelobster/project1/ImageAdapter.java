package com.robsterthelobster.project1;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

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

    }

    @Override
    public Cursor swapCursor(Cursor cursor){
        Cursor c = super.swapCursor(cursor);
        addToUrls(cursor);
        return c;
    }

    private void addToUrls(Cursor cursor){
        urls.clear();
        if(cursor!=null){
            while (cursor.moveToNext()){
                final String BASE = "http://image.tmdb.org/t/p/w185/";
                String path = BASE + cursor.getString(MovieFragment.COL_POSTER);

                System.out.println("OGtitle: " + cursor.getString(MovieFragment.COL_OG_TITLE));
                System.out.println("title: " + cursor.getString(MovieFragment.COL_TITLE));
                System.out.println("path: " + path);
                System.out.println(cursor.getString(MovieFragment.COL_POPULARITY));

                urls.add(path);
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