package com.erkutoguz.moviever_backend.service;

import com.erkutoguz.moviever_backend.dto.request.CreateWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.RenameWatchlistRequest;
import com.erkutoguz.moviever_backend.dto.request.WatchlistMovieRequest;
import com.erkutoguz.moviever_backend.dto.response.WatchlistResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class WatchlistService {

    private static final Logger log = LoggerFactory.getLogger(WatchlistService.class);
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

    //TODO buraya bi bak
    @CacheEvict(value = "retrieveAllWatchlists", allEntries = true)
    public void renameWatchlist(Long watchlistId, RenameWatchlistRequest request) {
        //TODO Burada movies ve watchlists alanlarını da update edip save etmek gerekebilir
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

    public List<WatchlistResponsePreview> retrieveWatchlistsPreview(String username) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Watchlist> watchlists = watchlistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Watchlist not found"));

        List<WatchlistResponsePreview> response = new ArrayList<>();
        for (int i = 0; i < watchlists.size(); i++) {
            response.add(WatchlistPreviewMapper.map(watchlists.get(i)));
        }
        return response;
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
