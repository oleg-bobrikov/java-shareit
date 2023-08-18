package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    User create(User user);

    Optional<User> findById(int userId);

    boolean hasEmail(String email);

    User update(User user);

    void deleteById(Integer userId);

    List<User> getAll();
}
