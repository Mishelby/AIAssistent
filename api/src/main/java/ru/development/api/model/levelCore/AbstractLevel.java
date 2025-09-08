package ru.development.api.model.levelCore;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.Queue;


@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public abstract sealed class AbstractLevel<T extends TaskType> implements ProgrammingLevelType
        permits JavaBeginnerLevel, JavaMiddleLevel {
    Queue<T> tasks;
    String description;
}
