package com.robsterthelobster.project1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.robsterthelobster.project1.Data.DbAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class ImageAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> urls = new ArrayList<String>();

    public ImageAdapter(Context context) {
        this.context = context;

        DbAdapter db = new DbAdapter(context);
        db.updateMovies();

        urls.addAll(db.getPosterPaths());
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
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