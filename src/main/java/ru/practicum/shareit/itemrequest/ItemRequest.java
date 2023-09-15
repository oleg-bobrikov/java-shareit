package ru.practicum.shareit.itemrequest;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", name = "created_date")
    @Builder.Default
    private OffsetDateTime createdDate = OffsetDateTime.now();
}
