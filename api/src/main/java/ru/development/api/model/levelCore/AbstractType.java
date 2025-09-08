package ru.development.api.model.levelCore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public sealed class AbstractType implements TaskType
        permits JavaBeginnerType, JavaMiddleType {

    String languageName;
    int difficultLevel;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;
}
