package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking as b where " +
            "b.id=?1 and " +
            "b.start<=CURRENT_TIMESTAMP and " +
            "b.end>CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findCurrentByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking as b where " +
            "b.id=?1 and " +
            "b.end<=CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findPastByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b from Booking as b where " +
            "b.id=?1 and " +
            "b.start>CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findFutureByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    @Query("select b from Booking as b inner join Item as i  on b.item = i where " +
            "i.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking as b inner join Item as i  on b.item = i where " +
            "i.owner.id = ?1 and " +
            "b.start<=CURRENT_TIMESTAMP and " +
            "b.end>CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findCurrentByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking as b inner join Item as i  on b.item = i where " +
            "i.owner.id = ?1 and " +
            "b.end<=CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findPastByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b from Booking as b inner join Item as i  on b.item = i where " +
            "i.owner.id = ?1 and " +
            "b.start>CURRENT_TIMESTAMP " +
            "order by b.start desc")
    List<Booking> findFutureByOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, Status status);

}