package ru.development.api.model.levelCore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
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
