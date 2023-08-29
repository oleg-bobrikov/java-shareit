package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByEmail(String email);

    @Query("select u from User as u where u.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<User> findByIdPessimisticRead(Long userId);
}