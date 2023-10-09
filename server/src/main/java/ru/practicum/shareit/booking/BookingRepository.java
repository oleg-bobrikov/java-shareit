package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDateDesc(Long bookerId, Pageable page);

    @Query("select b from Booking b where " +
            "b.booker.id = :bookerId and " +
            "b.startDate <= CURRENT_TIMESTAMP and " +
            "b.endDate > CURRENT_TIMESTAMP and " +
            "b.status in (ru.practicum.shareit.booking.Status.APPROVED, " +
            "             ru.practicum.shareit.booking.Status.REJECTED) " +
            "order by b.startDate desc ")
    List<Booking> findCurrentByBookerIdOrderByStartDateDesc(@Param("bookerId") long bookerId, Pageable page);

    @Query("select b from Booking b where " +
            "b.booker.id = :bookerId and " +
            "b.endDate <= CURRENT_TIMESTAMP and " +
            "b.status = ru.practicum.shareit.booking.Status.APPROVED " +
            "order by b.startDate desc")
    List<Booking> findPastByBookerIdOrderByStartDateDesc(@Param("bookerId") long bookerId, Pageable page);

    @Query(value = "select * from bookings as b where " +
            "b.booker_id = :bookerId and " +
            "b.item_id = :itemId and " +
            "b.start_date <= CURRENT_TIMESTAMP and " +
            "b.status = 'APPROVED' " +
            "order by b.start_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findLastBookedByBookerIdAndItemId(@Param("bookerId") long bookerId, @Param("itemId") long itemId);

    @Query("select b from Booking b where " +
            "b.booker.id = :bookerId and " +
            "b.startDate > CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findFutureByBookerIdOrderByStartDateDesc(@Param("bookerId") long bookerId, Pageable page);

    List<Booking> findAlByBooker_IdAndStatusOrderByStartDateDesc(Long bookerId, Status status, Pageable page);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item = i " +
            "where " +
            "i.owner.id = :ownerId " +
            "order by b.startDate desc")
    List<Booking> findAllByOwnerIdOrderByStartDateDesc(@Param("ownerId") long ownerId, Pageable page);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item = i " +
            "where " +
            "i.owner.id = :ownerId and " +
            "b.startDate <= CURRENT_TIMESTAMP and " +
            "b.endDate > CURRENT_TIMESTAMP and " +
            "b.status in (ru.practicum.shareit.booking.Status.APPROVED, " +
            "             ru.practicum.shareit.booking.Status.REJECTED) " +
            "order by b.startDate desc")
    List<Booking> findCurrentByOwnerIdOrderByStartDateDesc(@Param("ownerId") long ownerId, Pageable page);

    @Query(value = "select * from bookings as b " +
            "inner join items as i on b.item_id = i.id " +
            "where " +
            "i.id = :itemId and " +
            "b.start_date <= CURRENT_TIMESTAMP and " +
            "b.status = 'APPROVED' " +
            "order by b.start_date desc " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findLastByItemId(@Param("itemId") long itemId);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item = i " +
            "where " +
            "i.owner.id = :ownerId and " +
            "b.endDate <= CURRENT_TIMESTAMP and " +
            "b.status = ru.practicum.shareit.booking.Status.APPROVED " +
            "order by b.startDate desc")
    List<Booking> findPastByOwnerIdOrderByStartDateDesc(@Param("ownerId") long ownerId, Pageable page);

    @Query("select b from Booking as b " +
            "inner join Item as i on b.item= i " +
            "where " +
            "i.owner.id = :ownerId and " +
            "b.startDate > CURRENT_TIMESTAMP " +
            "order by b.startDate desc")
    List<Booking> findFutureByOwnerIdOrderByStartDateDesc(@Param("ownerId") long ownerId, Pageable page);

    @Query(value = "select * from bookings b " +
            "inner join items as i on b.item_id = i.id " +
            "where " +
            "i.id = :itemId and " +
            "b.start_date > CURRENT_TIMESTAMP and " +
            "b.status = 'APPROVED' " +
            "order by b.start_date " +
            "limit 1", nativeQuery = true)
    Optional<Booking> findFutureByItemId(@Param("itemId") long itemId);

    @Query(value = "select * from bookings as b " +
            "inner join items as i on b.item_id = i.id " +
            "where " +
            "i.id= :itemId and " +
            "b.end_date >= :start and " +
            "b.start_date <= :end and " +
            "b.status in ('WAITING', 'APPROVED') ",
            nativeQuery = true)
    List<Booking> findIntersectionPeriods(@Param("itemId") long itemId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDateDesc(Long ownerId, Status status, Pageable page);

}