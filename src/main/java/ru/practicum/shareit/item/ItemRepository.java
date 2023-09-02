package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndOwnerId(Long id, Long ownerId);

    List<Item> findByOwnerId(Long ownerId);

    @Query(" select i from Item as i " +
            "where i.isAvailable = true  and (lower(i.name) like lower(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%')))")
    List<Item> search(String text);

    @Query("select i from Item as i where i.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Item> findByIdPessimisticRead(Long itemId);

}

