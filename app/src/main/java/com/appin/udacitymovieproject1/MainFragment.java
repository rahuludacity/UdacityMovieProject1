package com.appin.udacitymovieproject1;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Downloading please wait...");
        pd.setCanceledOnTouchOutside(false);
        new MoviesAsyncTask().execute(MOST_POPULAR);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setHasFixedSize(true);

    }

    protected void sortList(String value) {
        new MoviesAsyncTask().execute(value);
    }

    public static class PosterHolder extends RecyclerView.ViewHolder {
        public final NetworkImageView imageView;

        public PosterHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.movieImage);
        }
    }

    protected class MoviesAsyncTask extends AsyncTask<String, Void, List<MovieDb>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected List<MovieDb> doInBackground(String... params) {
            TmdbMovies movies = new TmdbApi(getString(R.string.api_key)).getMovies();
            if (MOST_RATED.equals(params[0])) {
                MovieResultsPage movie = movies.getTopRatedMovies("en", 10);
                return movie.getResults();
            }
            MovieResultsPage movie = movies.getPopularMovieList("en", 10);
            return movie.getResults();
        }

        @Override
        protected void onPostExecute(List<MovieDb> moviesList) {
            super.onPostExecute(moviesList);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            if (moviesList != null && !moviesList.isEmpty()) {
                recyclerView.setAdapter(new MoviesGridAdater(moviesList));
            } else {
                Toast.makeText(getActivity(), "We are unable to get the information Currently", Toast.LENGTH_LONG).show();
            }
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
