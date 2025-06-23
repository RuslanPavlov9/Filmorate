package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false)
    private String name;

    @Column
    private LocalDate birthday;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Friendship> friendships = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    public void addFriend(User friend, String status) {
        if (this.equals(friend)) {
            throw new IllegalArgumentException("Пользователь не может добавить в друзья сам себя.");
        }
        Friendship friendship = new Friendship(this, friend, status);
        friendships.add(friendship);
        friend.getFriendships().add(new Friendship(friend, this, status));
    }

    public void removeFriend(User friend) {
        friendships.removeIf(fs -> fs.getFriend().equals(friend));
        friend.getFriendships().removeIf(fs -> fs.getFriend().equals(this));
    }

}