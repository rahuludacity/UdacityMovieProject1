package com.appin.udacitymovieproject1;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import info.movito.themoviedbapi.model.MovieDb;

public class DetailFragment extends Fragment {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(1);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.posterImageView);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView plot = (TextView) view.findViewById(R.id.plot);
        TextView releasedate = (TextView) view.findViewById(R.id.releaseDate);
        TextView rating = (TextView) view.findViewById(R.id.rating);

        imageView.setImageUrl("http://image.tmdb.org/t/p/w500" + getMovieDb().getBackdropPath(), mImageLoader);
        title.setText(getMovieDb().getOriginalTitle());
        plot.setText(getMovieDb().getOverview());
        releasedate.setText(getMovieDb().getReleaseDate());
        rating.setText(String.valueOf(getMovieDb().getVoteAverage()));
    }

    private MovieDb getMovieDb() {
        return MySingelton.getInstance().getMovieDb();
    }
}
