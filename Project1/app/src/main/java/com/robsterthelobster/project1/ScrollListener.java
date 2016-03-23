package com.robsterthelobster.project1;

/**
 * Created by robin on 3/22/2016.
 * SampleScrollListener.java from https://github.com/square/picasso/
 * Used to control what happens during scroll states
 */
import android.content.Context;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;

public class ScrollListener implements AbsListView.OnScrollListener {
    private final Context context;

    public ScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final Picasso picasso = Picasso.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // Do nothing.
    }
}
