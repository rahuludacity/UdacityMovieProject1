package com.appin.udacitymovieproject1;


import info.movito.themoviedbapi.model.MovieDb;

public class MySingelton {

    private static MySingelton instance = null;
    private MovieDb movieDb;

    private MySingelton() {

    }

    public synchronized static MySingelton getInstance() {
        if (instance == null) {
            instance = new MySingelton();
            return instance;
        }
        return instance;
    }

    public MovieDb getMovieDb() {
        return movieDb;
    }

    public void setMovieDb(MovieDb movieDb) {
        this.movieDb = movieDb;
    }
}
