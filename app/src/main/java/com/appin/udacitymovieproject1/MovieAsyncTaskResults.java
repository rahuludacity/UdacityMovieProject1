package com.appin.udacitymovieproject1;


import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;

public class MovieAsyncTaskResults {

    private List<MovieDb> moviesList;

    public MovieAsyncTaskResults(List<MovieDb> moviesList) {
        this.moviesList = moviesList;
    }


    public List<MovieDb> getMoviesList() {
        return moviesList;
    }
}
