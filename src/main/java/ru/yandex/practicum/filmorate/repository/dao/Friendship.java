package ru.yandex.practicum.filmorate.repository.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friends", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "friend_id"})
})
@Data
@NoArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    public Friendship(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }
}