package ru.development.api.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.development.api.model.enums.ProgrammingLanguage;

import static java.util.Objects.nonNull;

@Converter(autoApply = true)
public class ProgrammingLanguageConverter implements AttributeConverter<ProgrammingLanguage, String> {

    @Override
    public String convertToDatabaseColumn(ProgrammingLanguage attribute) {
        if (nonNull(attribute)) {
            return ProgrammingLanguage.fromEnumToString(attribute);
        }
        return null;
    }

    @Override
    public ProgrammingLanguage convertToEntityAttribute(String dbData) {
        if (nonNull(dbData)) {
            return ProgrammingLanguage.fromStringToEnum(dbData);
        }
        return null;
    }
}
