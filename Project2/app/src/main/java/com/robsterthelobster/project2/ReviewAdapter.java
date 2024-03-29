package com.robsterthelobster.project2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.robsterthelobster.project2.models.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robin on 4/3/2016.
 */
public class ReviewAdapter extends BaseAdapter{

    private final Context mContext;
    private final List<Review> reviews = new ArrayList<Review>();

    public ReviewAdapter(Context context, List<Review> list) {
        mContext = context;

        reviews.addAll(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(mContext == null){
            return convertView;
        }
        Button button = (Button) convertView;
        if(button == null){
            button = new Button(mContext);
        }

        final Review review = getItem(position);

        button.setText("Review by " + review.getAuthor());

        button.setAllCaps(false);

        button.setGravity(Gravity.LEFT);
        button.setTextAppearance(mContext, android.R.style.TextAppearance_Medium);
        button.setCompoundDrawablePadding(16);
        if(Build.VERSION.SDK_INT >= 16)
            button.setBackground(mContext.getResources().getDrawable(R.drawable.button_selector));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(review.getUrl());
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
        return reviews.size();
    }

    @Override
    public Review getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
