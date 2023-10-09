package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByIdAndOwnerId(long id, long ownerId);

    List<Item> findByOwnerId(long ownerId);

    @Query(" select i from Item as i " +
            "where i.isAvailable = true  and (lower(i.name) like lower(concat('%', :text, '%')) " +
            " or lower(i.description) like lower(concat('%', :text, '%')))")
    List<Item> search(@Param("text") String text);

    List<Item> findByItemRequestId(long itemRequestId);

}

