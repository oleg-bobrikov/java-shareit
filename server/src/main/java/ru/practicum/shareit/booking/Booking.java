package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime startDate;

    @Column(name = "end_date", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime endDate;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;

}