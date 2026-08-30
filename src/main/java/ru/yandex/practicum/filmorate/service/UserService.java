package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователи ID {} и ID {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователи ID {} и ID {} удалены из друзей друг у друга", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        User user = getById(userId);
        return user.getFriends().stream()
                .map(userStorage::getById)
                .flatMap(Optional::stream)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = getById(userId);
        User otherUser = getById(otherId);

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherFriends = otherUser.getFriends();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getById)
                .flatMap(Optional::stream)
                .toList();
    }
}