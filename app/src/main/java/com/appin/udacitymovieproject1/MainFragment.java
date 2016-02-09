package com.appin.udacitymovieproject1;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.squareup.otto.Subscribe;

import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

public class MainFragment extends Fragment implements Constants {

    private RecyclerView recyclerView;
    private ProgressDialog pd;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        MovieEventBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MovieEventBus.getInstance().unregister(this);
    }


    @Subscribe
    public void onAsyncTaskResult(MovieAsyncTaskResults event) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
        if (event.getMoviesList() != null && !event.getMoviesList().isEmpty()) {
            recyclerView.setAdapter(new MoviesGridAdater(event.getMoviesList()));
        } else {
            Toast.makeText(getActivity(), "We are unable to get the information Currently", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Downloading please wait...");
        pd.setCanceledOnTouchOutside(false);
        getDetails(MOST_POPULAR);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setHasFixedSize(true);
    }

    private void getDetails(String value) {
        if (isOnline(getActivity())) {
            new MovieAsyncTask(pd, getResources()).execute(value);

        } else {
            Toast.makeText(getActivity(), "Please check your network conenction", Toast.LENGTH_SHORT).show();
        }
    }

    protected void sortList(String value) {
        getDetails(value);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static class PosterHolder extends RecyclerView.ViewHolder {
        public final NetworkImageView imageView;

        public PosterHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.movieImage);
        }
    }

    private class MoviesGridAdater extends RecyclerView.Adapter<PosterHolder> {

        private final RequestQueue mRequestQueue;
        private final ImageLoader mImageLoader;
        private final List<MovieDb> moviesList;

        public MoviesGridAdater(List<MovieDb> moviesList) {
            this.moviesList = moviesList;
            mRequestQueue = Volley.newRequestQueue(getActivity());

            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(30);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }

        @Override
        public PosterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new PosterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false));
        }

        @Override
        public void onBindViewHolder(PosterHolder holder, int position) {
            holder.imageView.setErrorImageResId(R.drawable.error_drawable);
            holder.imageView.setDefaultImageResId(R.drawable.default_image);
            holder.imageView.setImageUrl("http://image.tmdb.org/t/p/w500" + moviesList.get(position).getPosterPath(), mImageLoader);
            holder.imageView.setTag(moviesList.get(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MySingelton.getInstance().setMovieDb((MovieDb) v.getTag());
                    Intent i = new Intent(getActivity(), DetailActivity.class);
                    startActivity(i);
                }
            });


        }


        @Override
        public int getItemCount() {
            return moviesList.size();
        }


    }


}
