package com.robsterthelobster.project2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.robsterthelobster.project2.models.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robin on 4/3/2016.
 */
public class TrailerAdapter extends BaseAdapter{

    private static final String BASE_URL = "https://www.youtube.com/watch?v=";
    private final Context mContext;
    private final List<Trailer> trailers = new ArrayList<Trailer>();

    public TrailerAdapter(Context context, List<Trailer> list) {
        mContext = context;

        trailers.addAll(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Button button = (Button) convertView;
        if(button == null){
            button = new Button(mContext);
        }

        Trailer trailer = getItem(position);
        final String url = BASE_URL + trailer.getKey();

        button.setText("Trailer " + (position + 1));

        Drawable img = mContext.getResources().getDrawable(R.drawable.ic_play_arrow_24dp);
        img.setBounds(0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight());
        button.setCompoundDrawables(img, null, null, null);
        button.setAllCaps(false);

        button.setGravity(Gravity.LEFT);
        button.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
        button.setCompoundDrawablePadding(16);
        if(Build.VERSION.SDK_INT >= 16)
            button.setBackground(mContext.getResources().getDrawable(R.drawable.button_selector));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                    mContext.startActivity(intent);
                }
            }
        });
        return button;
    }


    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public Trailer getItem(int position) {
        return trailers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
