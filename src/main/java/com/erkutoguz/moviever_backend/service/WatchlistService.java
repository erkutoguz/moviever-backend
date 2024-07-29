package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.CreateWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.WatchlistMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponse;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponseWithMovies;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.model.Watchlist;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.repository.WatchlistRepository;
import com.erkutoguz.moviever_backend.util.MovieMapper;
import com.erkutoguz.moviever_backend.util.WatchlistMapper;
import com.erkutoguz.moviever_backend.util.WatchlistWithMoviesMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    public WatchlistService(WatchlistRepository watchlistRepository, MovieRepository movieRepository, UserRepository userRepository) {
        this.watchlistRepository = watchlistRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    public List<WatchlistResponse> retrieveUserWatchlists(Principal principal) {
        User user = (User) userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        return WatchlistMapper.map(watchlists);
    }

    public List<WatchlistResponseWithMovies> retrieveWatchlistsPreview() {
        User user = (User) userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        List<WatchlistResponseWithMovies> response = new ArrayList<>();

        for (int i = 0; i < watchlists.size(); i++) {
            response.add(WatchlistWithMoviesMapper.map(watchlists.get(i)));
        }
        return response;
    }


    public WatchlistResponseWithMovies retrieveWatchlist(Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId).orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        return new WatchlistResponseWithMovies(watchlist.getId(),
                MovieMapper.map(watchlist.getMovies()), watchlist.getWatchlistName());
    }



    public void createWatchlist(CreateWatchlistRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Watchlist watchlist = new Watchlist();
        User user = (User) userRepository.findByUsername(authentication.getName()).orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        watchlist.setUser(user);
        watchlist.setWatchlistName(request.watchlistName());
        watchlistRepository.save(watchlist);
    }

    public void deleteWatchlist(Long watchlistId) {
        watchlistRepository.deleteById(watchlistId);
    }

    public void addMovieToWatchlist(Long watchlistId, WatchlistMovieRequest request){
        Movie movie = movieRepository.findById(request.movieId()).orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Watchlist watchlist = watchlistRepository.findById(watchlistId).orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        if(!watchlist.getMovies().contains(movie)) {
            watchlist.addMovie(movie);
            watchlistRepository.save(watchlist);
        }
    }

    public void deleteMovieFromWatchlist(Long watchlistId, Long movieId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId).orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        watchlist.removeMovie(movieId);
        watchlistRepository.save(watchlist);
    }
}
