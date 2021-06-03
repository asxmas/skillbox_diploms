package ru.skillbox.team13.entity;

import lombok.Data;


import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "friendship_status")
public class FriendshipStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private LocalDateTime time;
    private String name;

    @Enumerated(EnumType.STRING)
    private Code code;

    public enum Code {
        REQUEST,
        FRIEND,
        BLOCKED,
        DECLINED,
        SUBSCRIBED
    }
}
