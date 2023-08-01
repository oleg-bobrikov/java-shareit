package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int maxId;

    @Override
    public User create(User user) {
        user.setId(++maxId);
        users.put(user.getId(), user.toBuilder().build()); //protect
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findById(int userId) {
        User user = users.get(userId);
        // it is necessary to protect the object from changes that's why we return a copy of object
        return user == null ? Optional.empty() : Optional.of(user.toBuilder().build());
    }

    @Override
    public boolean hasEmail(String email) {
        return emails.contains(email);
    }

    @Override
    public User update(User user) {
        String oldEmail = users.get(user.getId()).getEmail();
        emails.remove(oldEmail);
        users.put(user.getId(), user.toBuilder().build()); //protect
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public void deleteById(Integer userId) {
        User user = users.get(userId);
        emails.remove(user.getEmail());
        users.remove(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
