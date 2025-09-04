package ru.development.api.model.enums;

import lombok.Getter;

@Getter
public enum ProgrammingLevel {
    BEGINNER("first level"),
    MIDDLE("middle level"),
    SENIOR("senior level");

    String description;

    ProgrammingLevel(String description) {
        this.description = description;
    }

    public static ProgrammingLevel fromStringToEnum(String value) {
        for (ProgrammingLevel level : ProgrammingLevel.values()) {
            if (level.description.equalsIgnoreCase(value)) {
                return level;
            }
        }

        throw new IllegalArgumentException(value + "  is not a valid programming level");
    }

    public static String fromEnumToString(ProgrammingLevel value) {
        for (ProgrammingLevel level : ProgrammingLevel.values()) {
            if (level.description.equalsIgnoreCase(value.name())) {
                return level.description;
            }
        }

        throw new IllegalArgumentException(value + "  is not a valid programming level");
    }
}
