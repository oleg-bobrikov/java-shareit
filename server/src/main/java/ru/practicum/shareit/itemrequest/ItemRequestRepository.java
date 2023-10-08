package ru.practicum.shareit.itemrequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterIdOrderByCreatedDateDesc(long requesterId);

    @Query("select r from ItemRequest r where r.requester.id <> :requesterId order by r.createdDate desc")
    Page<ItemRequest> findOthersByRequesterIdOrderByCreatedDateDesc(@Param("requesterId") long requesterId, Pageable page);

}