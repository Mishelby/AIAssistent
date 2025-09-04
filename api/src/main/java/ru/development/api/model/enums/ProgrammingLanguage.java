package ru.development.api.model.enums;

import lombok.Getter;

@Getter
public enum ProgrammingLanguage {
    JAVA("java"),
    PYTHON("python"),
    JAVASCRIPT("javascript");

    String description;

    ProgrammingLanguage(String description) {
        this.description = description;
    }

    public static ProgrammingLanguage fromStringToEnum(String value) {
        for (ProgrammingLanguage pl : ProgrammingLanguage.values()) {
            if (pl.description.equalsIgnoreCase(value)) {
                return pl;
            }
        }
        throw new IllegalArgumentException(value + " is not a valid programming Language");
    }

    public static String fromEnumToString(ProgrammingLanguage value) {
       for (ProgrammingLanguage pl : ProgrammingLanguage.values()) {
           if(pl.description.equalsIgnoreCase(value.name())) {
               return pl.description;
           }
       }

       throw new IllegalArgumentException(value + " is not a valid programming Language");
    }
}
