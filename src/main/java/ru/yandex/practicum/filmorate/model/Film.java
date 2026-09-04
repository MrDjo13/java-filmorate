package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    Long id;

    @NonNull
    @NotBlank(message = "Название фильма не может быть пустым")
    String name;

    @NonNull
    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Максимальная длина описания — "
            + MAX_DESCRIPTION_LENGTH + " символов")
    String description;

    @NonNull
    LocalDate releaseDate;

    @NonNull
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    Integer duration;

    @Builder.Default
    Set<Long> likes = new HashSet<>();

}