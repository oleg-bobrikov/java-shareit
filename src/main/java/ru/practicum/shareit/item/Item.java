package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.itemrequest.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items", indexes = {
        @Index(name = "id_owner_id_idx", columnList = "id, owner_id"),
        @Index(name = "owner_id_idx", columnList = "owner_id")
})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "is_available")
    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

}
