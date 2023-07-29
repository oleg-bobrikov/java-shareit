package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository {
    User create(User user);

    Optional<User> findUserById(int userId);

    boolean hasEmail(String email);

    User update(User user);

    void deleteUserById(Integer userId);
    List<User> getUsers();
}
