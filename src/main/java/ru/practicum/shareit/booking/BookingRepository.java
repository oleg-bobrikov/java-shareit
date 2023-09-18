package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId, Pageable page);

    @Query("select b from Booking b where " +
            "b.booker.id = ?1 and " +
            "b.startDate <= CURRENT_TIMESTAMP and " +
            "b.endDate > CURRENT_TIMESTAMP and " +
            "b.status in (ru.practicum.shareit.booking.Status.APPROVED, " +
            "             ru.practicum.shareit.booking.Status.REJECTED) " +
            "order by b.startDate desc ")
    List<Booking> findCurrentByBookerIdOrderByStartDateDesc(Long bookerId, Pageable page);

    @Query("select b from Booking b where " +
            "b.booker.id = ?1 and " +
            "b.endDate <= CURRENT_TIMESTAMP and " +
            "b.status = ru.practicum.shareit.booking.Status.APPROVED " +
            "order by b.startDate desc")
    List<Booking> findPastByBookerIdOrderByStartDateDesc(Long bookerId, Pageable page);

    @Query(value = "select * from bookings as b where " +
            "b.booker_id = ?1 and " +
            "b.item_id = ?2 and " +
            "b.start_date <= CURRENT_TIMESTAMP and " +
            "b.status = 'APPROVED' " +
            "order by b.start_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findLastBookedByBookerIdAndItemId(Long bookerId, Long itemId);

    @Query("select b from Booking b where " +
            "b.booker.id = ?1 and " +
            "b.startDate > CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findFutureByBookerIdOrderByStartDateDesc(Long bookerId, Pageable page);

    List<Booking> findAlByBooker_IdAndStatusOrderByStartDateDesc(Long bookerId, Status status, Pageable page);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item = i " +
            "where " +
            "i.owner.id = ?1 " +
            "order by b.startDate desc")
    List<Booking> findAllByOwnerIdOrderByStartDateDesc(Long ownerId, Pageable page);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item = i " +
            "where " +
            "i.owner.id = ?1 and " +
            "b.startDate <= CURRENT_TIMESTAMP and " +
            "b.endDate > CURRENT_TIMESTAMP and " +
            "b.status in (ru.practicum.shareit.booking.Status.APPROVED, " +
            "             ru.practicum.shareit.booking.Status.REJECTED) " +
            "order by b.startDate desc")
    List<Booking> findCurrentByOwnerIdOrderByStartDateDesc(Long ownerId, Pageable page);

    @Query(value = "select * from bookings as b " +
            "inner join items as i on b.item_id = i.id " +
            "where " +
            "i.id = ?1 and " +
            "b.start_date <= CURRENT_TIMESTAMP and " +
            "b.status = 'APPROVED' " +
            "order by b.start_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findLastByItemId(Long itemId);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item = i " +
            "where " +
            "i.owner.id = ?1 and " +
            "b.endDate <= CURRENT_TIMESTAMP and " +
            "b.status = ru.practicum.shareit.booking.Status.APPROVED " +
            "order by b.startDate desc")
    List<Booking> findPastByOwnerIdOrderByStartDateDesc(Long ownerId, Pageable page);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item= i " +
            "where " +
            "i.owner.id = ?1 and " +
            "b.startDate > CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findFutureByOwnerIdOrderByStartDateDesc(long ownerId, Pageable page);

    @Query(value = "select * from bookings b " +
            "inner join items as i on b.item_id = i.id " +
            "where " +
            "i.id = ?1 and " +
            "b.start_date > CURRENT_TIMESTAMP and " +
            "b.status = 'APPROVED' " +
            "order by b.start_date " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findFutureByItemId(Long itemId);

    @Query(value = "select * from bookings as b " +
            "inner join items as i on b.item_id = i.id " +
            "where " +
            "i.id= ?1 and " +
            "b.end_date >= ?2 and " +
            "b.start_date <= ?3 and " +
            "b.status in ('WAITING', 'APPROVED') ",
            nativeQuery = true)
    List<Booking> findIntersectionPeriods(Long itemId, OffsetDateTime start, OffsetDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Long ownerId, Status status, Pageable page);

}