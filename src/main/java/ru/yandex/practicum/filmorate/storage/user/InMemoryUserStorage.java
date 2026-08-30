package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 0;

    @Override
    public User create(User user) {
        user.setId(++currentId);
        normalizeUserName(user);
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с ID: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (oldUser == null) {
            log.warn("Пользователь с ID {} не найден для обновления", user.getId());
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        normalizeUserName(user);
        if (user.getFriends().isEmpty() && !oldUser.getFriends().isEmpty()) {
            user.setFriends(oldUser.getFriends());
        }
        users.put(user.getId(), user);
        log.info("Пользователь с ID {} успешно обновлен", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        log.info("Пользователь с ID {} удален", id);
    }

    private void normalizeUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}