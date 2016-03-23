package com.robsterthelobster.project1;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import com.robsterthelobster.project1.Data.DbAdapter;

public class MainActivity extends Activity{

    DbAdapter db;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        GridView gv = (GridView) findViewById(R.id.grid_view);
//        gv.setAdapter(new ImageAdapter(this));
//        gv.setOnScrollListener(new ScrollListener(this));

        //FetchMovieData fetchMovieData = new FetchMovieData();
        db = new DbAdapter(this);
        db.updateMovies();
        //db.getWritableDatabase();

        TextView tv = (TextView)this.findViewById(R.id.mTextView);
        Cursor cur = db.fetch();
        cur.moveToFirst();
        while(!cur.isAfterLast()){
            tv.append(cur.getString(0) + "\n");
            cur.moveToNext();
        }

    }

}
