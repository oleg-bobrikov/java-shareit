package ru.practicum.shareit.itemrequest;

import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant created = Instant.now();
}
