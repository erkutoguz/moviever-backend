package com.erkutoguz.moviever_backend.config;

import com.erkutoguz.moviever_backend.model.*;
import com.erkutoguz.moviever_backend.repository.CategoryRepository;
import com.erkutoguz.moviever_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class Initializer {

    @Value("${spring.user-admin.password}")
    private String adminPassword;

    @Value("${spring.user-admin.username}")
    private String adminUsername;

    @Value("${spring.user-admin.email}")
    private String adminEmail;


    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    public Initializer(UserRepository userRepository,CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent readyEvent) {

        if(categoryRepository.count() == 0) {
            Category ACTION = new Category();
            ACTION.setCategoryName(CategoryType.ACTION);

            Category COMEDY = new Category();
            COMEDY.setCategoryName(CategoryType.COMEDY);

            Category DRAMA = new Category();
            DRAMA.setCategoryName(CategoryType.DRAMA);

            Category HORROR = new Category();
            HORROR.setCategoryName(CategoryType.HORROR);

            Category HISTORY = new Category();
            HISTORY.setCategoryName(CategoryType.HISTORY);

            Category ROMANCE = new Category();
            ROMANCE.setCategoryName(CategoryType.ROMANCE);

            Category SCIENCE_FICTION = new Category();
            SCIENCE_FICTION.setCategoryName(CategoryType.SCIENCE_FICTION);

            Category THRILLER = new Category();
            THRILLER.setCategoryName(CategoryType.THRILLER);

            Category ANIMATION = new Category();
            ANIMATION.setCategoryName(CategoryType.ANIMATION);

            Category FANTASY = new Category();
            FANTASY.setCategoryName(CategoryType.FANTASY);

            Category DOCUMENTARY = new Category();
            DOCUMENTARY.setCategoryName(CategoryType.DOCUMENTARY);

            Category ADVENTURE = new Category();
            ADVENTURE.setCategoryName(CategoryType.ADVENTURE);

            Category MYSTERY = new Category();
            MYSTERY.setCategoryName(CategoryType.MYSTERY);

            Category CRIME = new Category();
            CRIME.setCategoryName(CategoryType.CRIME);

            Category MUSIC = new Category();
            MUSIC.setCategoryName(CategoryType.MUSIC);

            Category OTHER = new Category();
            OTHER.setCategoryName(CategoryType.OTHER);

            List<Category> categoryList = List.of(ACTION, COMEDY, DRAMA, HORROR, HISTORY, ROMANCE,
                    SCIENCE_FICTION, THRILLER, ANIMATION, FANTASY, DOCUMENTARY, ADVENTURE, MYSTERY,
                    CRIME, MUSIC, OTHER);
            categoryRepository.saveAll(categoryList);
        }

        if (!userRepository.existsByUsername(adminUsername)) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            User admin = new User();
            admin.setEnabled(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            admin.setAccountNonExpired(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setEmail(adminEmail);
            admin.setUsername(adminUsername);
            admin.setFirstname("Erkut");
            admin.setLastname("Oğuz");
            admin.setRoles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER));
            admin.setPassword(passwordEncoder.encode(adminPassword));

            User savedUser = userRepository.save(admin);
        }

    }
}
