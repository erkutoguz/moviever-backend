package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.WatchlistResponseWithMovies;
import com.erkutoguz.moviever_backend.model.Watchlist;

import java.util.List;

public interface WatchlistWithMoviesMapper {

    static WatchlistResponseWithMovies map(Watchlist watchlist) {
        if (watchlist == null) return null;
        if (watchlist.getMovies().size() < 6) {
            return new WatchlistResponseWithMovies(watchlist.getId(), MovieMapper.map(watchlist.getMovies()) ,watchlist.getWatchlistName());
        }
        return new WatchlistResponseWithMovies(watchlist.getId(), MovieMapper.map(watchlist.getMovies().subList(0,6)), watchlist.getWatchlistName());

    }
    static List<WatchlistResponseWithMovies> map(List<Watchlist> watchlists) {
        if (watchlists == null) return null;
        return watchlists.stream().map(WatchlistWithMoviesMapper::map).toList();
    }
}
