package com.erkutoguz.moviever_backend.util;

import com.erkutoguz.moviever_backend.dto.response.AdminWatchlistResponse;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponse;
import com.erkutoguz.moviever_backend.model.Watchlist;

import java.util.List;

public interface WatchlistMapper {
    static WatchlistResponse map(Watchlist watchlist) {
        if (watchlist == null) return null;
        return new WatchlistResponse(watchlist.getId(), watchlist.getWatchlistName());
    }
    static List<WatchlistResponse> map(List<Watchlist> watchlists) {
        if (watchlists == null) return null;
        return watchlists.stream().map(WatchlistMapper::map).toList();
    }
    static AdminWatchlistResponse mapToAdminWatchlistResponse(Watchlist watchlist) {
        if(watchlist == null) return null;
        return new AdminWatchlistResponse(watchlist.getId(), watchlist.getUser().getId(),
                watchlist.getUser().getUsername(), watchlist.getWatchlistName());
    }
    static List<AdminWatchlistResponse> mapToAdminWatchlistResponse(List<Watchlist> watchlists) {
        if(watchlists == null) return null;
        return watchlists.stream().map(WatchlistMapper::mapToAdminWatchlistResponse).toList();
    }
}
