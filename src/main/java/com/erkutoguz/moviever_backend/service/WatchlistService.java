package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.CreateWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.RenameWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.WatchlistMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponsePreview;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponseWithMovies;
import com.erkutoguz.moviever_backend.exception.AccessDeniedException;
import com.erkutoguz.moviever_backend.exception.ResourceNotFoundException;
import com.erkutoguz.moviever_backend.model.Movie;
import com.erkutoguz.moviever_backend.model.Role;
import com.erkutoguz.moviever_backend.model.User;
import com.erkutoguz.moviever_backend.model.Watchlist;
import com.erkutoguz.moviever_backend.repository.MovieRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import com.erkutoguz.moviever_backend.repository.WatchlistRepository;
import com.erkutoguz.moviever_backend.util.MovieMapper;
import com.erkutoguz.moviever_backend.util.WatchlistMapper;
import com.erkutoguz.moviever_backend.util.WatchlistPreviewMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> retrieveUserWatchlists(Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> map = new HashMap<>();

        Page<Watchlist> watchlistsPage = watchlistRepository.findByUserId(user.getId(), pageable);
        map.put("watchlists", WatchlistMapper.map(watchlistsPage.getContent()));
        map.put("totalItems", watchlistsPage.getTotalElements());
        map.put("totalPages", watchlistsPage.getTotalPages());
        return map;
    }

    @CacheEvict(value = "retrieveAllWatchlists", allEntries = true)
    public void renameWatchlist(Long watchlistId, RenameWatchlistRequest request) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(!currentUser.equals(watchlist.getUser().getUsername()) && !authorities.contains(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You don't have permission to see this watchlist");
        }

        watchlist.setWatchlistName(request.watchlistName());
        watchlistRepository.save(watchlist);
    }

    public Map<String, Object> retrieveWatchlistsPreview(String username, int page ,int size) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(page, size);

        Page<Watchlist> watchlistsPage = watchlistRepository.findByUserId(user.getId(), pageable);

        Map<String, Object> map = new HashMap<>();

        List<WatchlistResponsePreview> response = new ArrayList<>();
        for (int i = 0; i < watchlistsPage.getContent().size(); i++) {
            response.add(WatchlistPreviewMapper.map(watchlistsPage.getContent().get(i)));
        }

        map.put("watchlistPreviews", response);
        map.put("totalItems", watchlistsPage.getTotalElements());
        map.put("totalPages", watchlistsPage.getTotalPages());

        return map;
    }

    public WatchlistResponseWithMovies retrieveWatchlist(Long watchlistId, int page, int size) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(!currentUser.equals(watchlist.getUser().getUsername()) && !authorities.contains(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You don't have permission to see this watchlist");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Movie> moviePage = movieRepository.findByWatchlistId(watchlist.getId(), pageable);

        return new WatchlistResponseWithMovies(watchlist.getId(), moviePage.getTotalElements(), moviePage.getTotalPages(),
                MovieMapper.map(moviePage), watchlist.getWatchlistName());
    }


    @CacheEvict(value = "retrieveAllWatchlists", allEntries = true)
    public void createWatchlist(CreateWatchlistRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Watchlist watchlist = new Watchlist();
        User user = (User) userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        watchlist.setUser(user);
        watchlist.setWatchlistName(request.watchlistName());
        watchlistRepository.save(watchlist);
    }

    @CacheEvict(value = "retrieveAllWatchlists", allEntries = true)
    public void deleteWatchlist(Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if(!currentUser.equals(watchlist.getUser().getUsername()) && !authorities.contains(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You don't have permission to see this watchlist");
        }
        List<Movie> movies = watchlist.getMovies().stream().toList();
        movies.forEach(m -> m.removeFromWatchlist(watchlist));
        movieRepository.saveAll(movies);

        watchlistRepository.deleteById(watchlistId);
    }

    public void addMovieToWatchlist(Long watchlistId, WatchlistMovieRequest request){
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        if(!watchlist.getMovies().contains(movie)) {
            watchlist.addMovie(movie);
            watchlistRepository.save(watchlist);
        }
    }

    public void deleteMovieFromWatchlist(Long watchlistId, Long movieId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        movie.removeFromWatchlist(watchlist);
        watchlistRepository.save(watchlist);
        movieRepository.save(movie);
    }

    @Cacheable(value = "retrieveAllWatchlists", key = "#root.methodName + '-' + #page + '-' + #size")
    public Map<String, Object> retrieveAllWatchlists(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        final Page<Watchlist> watchlists = watchlistRepository.findAllByOrderByIdAsc(pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("watchlists", WatchlistMapper.mapToAdminWatchlistResponse(watchlists.getContent()));
        map.put("totalItems", watchlists.getTotalElements());
        map.put("totalPages", watchlists.getTotalPages());
        return map;
    }
}
