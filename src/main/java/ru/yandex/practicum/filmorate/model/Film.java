package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {

    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likesUsers = new HashSet<>();
    private int likes = 0;
    private MPA mpa;
    private Set<Genre> genres;

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Set<Integer> likesUsers,
                MPA mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesUsers = likesUsers;
        this.mpa = mpa;
        this.genres = genres;
    }
}
