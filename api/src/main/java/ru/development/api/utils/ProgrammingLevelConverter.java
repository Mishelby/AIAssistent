package ru.development.api.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.development.api.model.enums.ProgrammingLevel;

import static java.util.Objects.nonNull;

@Converter(autoApply = true)
public class ProgrammingLevelConverter implements AttributeConverter<ProgrammingLevel, String> {

    @Override
    public String convertToDatabaseColumn(ProgrammingLevel attribute) {
        if (nonNull(attribute)) {
            return ProgrammingLevel.fromEnumToString(attribute);
        }
        return null;
    }

    @Override
    public ProgrammingLevel convertToEntityAttribute(String dbData) {
        if(nonNull(dbData)) {
            return ProgrammingLevel.fromStringToEnum(dbData);
        }
        return null;
    }
}
