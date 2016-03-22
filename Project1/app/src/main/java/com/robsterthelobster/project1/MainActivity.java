package com.robsterthelobster.project1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class MainActivity extends Activity{

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gv = (GridView) findViewById(R.id.grid_view);
        gv.setAdapter(new ImageAdapter(this));
        //gv.setOnScrollListener(new SampleScrollListener(this));
    }
}
