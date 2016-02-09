package com.appin.udacitymovieproject1;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.squareup.otto.Produce;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class MovieAsyncTask extends AsyncTask<String, Void, List<MovieDb>> implements Constants {


    private final ProgressDialog pd;
    private final Resources res;

    public MovieAsyncTask(ProgressDialog pd, Resources res) {
        this.pd = pd;
        this.res = res;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.show();
    }

    @Override
    protected List<MovieDb> doInBackground(String... params) {
        TmdbMovies movies = new TmdbApi(res.getString(R.string.api_key)).getMovies();
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
        MovieEventBus.getInstance().post(produceMovieResultsEvent(moviesList));

    }

    @Produce
    public MovieAsyncTaskResults produceMovieResultsEvent(List<MovieDb> moviesList) {
        return new MovieAsyncTaskResults(moviesList);
    }
}
