package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> emails = new HashMap<>();
    private int maxId;

    @Override
    public User create(User user) {
        user.setId(++maxId);
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user);
        return user;
    }

    @Override
    public User findById(int userId) {
        return users.get(userId);
    }

    @Override
    public User findByEmail(String email) {
        return emails.get(email);
    }
}
