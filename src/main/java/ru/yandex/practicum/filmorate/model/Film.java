package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "films")
@Data
@NoArgsConstructor
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column
    private int duration;

    @ManyToOne
    @JoinColumn(name = "mpa_id", nullable = false)
    private Mpa mpa;

    @ManyToMany
    @JoinTable(
            name = "film_genres",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

    @OneToMany(mappedBy = "film", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    public int getLikesCount() {
        return likes.size();
    }

}
