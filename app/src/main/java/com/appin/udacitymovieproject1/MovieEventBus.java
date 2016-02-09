package com.appin.udacitymovieproject1;


import com.squareup.otto.Bus;

public final class MovieEventBus {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }
}
