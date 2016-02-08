package com.appin.udacitymovieproject1;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpacingDecoration extends RecyclerView.ItemDecoration {


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(2, 4, 2, 4);
    }
}
