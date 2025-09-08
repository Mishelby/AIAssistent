package ru.development.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.development.api.model.*;
import ru.development.api.model.enums.ProgrammingLanguage;
import ru.development.api.model.enums.ProgrammingLevel;
import ru.development.api.repository.GradeEntityRepository;
import ru.development.api.repository.ProgrammingLanguageRepository;
import ru.development.api.repository.ProgrammingLevelRepository;
import ru.development.api.repository.UserEntityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserEntityRepository userRepository;
    private final ProgrammingLanguageRepository languageRepository;
    private final ProgrammingLevelRepository levelRepository;
    private final GradeEntityRepository gradeRepository;

    @Transactional
    public UserDto createUser(CreateUserRequest userRequest) {
        UserEntity userEntity = UserEntity.builder()
                .userName(userRequest.getUserName())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .build();

        ChatEntity chatEntity = ChatEntity.builder()
                .user(userEntity)
                .chatNumber(userRequest.getChatNumber())
                .build();

        userEntity.setChat(chatEntity);
        UserEntity savedUser = userRepository.save(userEntity);

        return UserDto.builder()
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .userName(savedUser.getUserName())
                .build();
    }

    @Transactional
    public void buildAndSafeInfoAboutUser(User user, String languageName, String levelName) {
        log.info("[USER INFO] Building and safe information about user {}", user.getUserName());

        log.info("[USER INFO] Сохранение данных о пользователе: {}, поток: {}",
                user.getUserName(),
                Thread.currentThread().getName()
        );

        UserEntity userEntity = userRepository.findByUserName(user.getUserName())
                .orElseThrow();

        ProgrammingLanguageEntity languageEntity = languageRepository.findByLanguageName(
                ProgrammingLanguage.fromStringToEnum(languageName)
        ).orElseThrow();

        ProgrammingLevelEntity levelEntity = levelRepository.findByLevelName(
                ProgrammingLevel.fromStringToEnum(levelName)
        ).orElseThrow();

        GradeEntity gradeEntity = getGradeEntity(userEntity, languageEntity, levelEntity);

        GradeEntity savedGrade = gradeRepository.save(gradeEntity);
        log.info("[USER INFO] Данные пользователя были сохранены! {}", savedGrade);

    }

    private static GradeEntity getGradeEntity(UserEntity userEntity,
                                              ProgrammingLanguageEntity languageEntity,
                                              ProgrammingLevelEntity levelEntity
    ) {
        return GradeEntity.builder()
                .user(userEntity)
                .language(languageEntity)
                .programmingLevel(levelEntity)
                .build();

    }

    public UserDto findByUserName(String userName) {
        UserEntity userEntity = userRepository.findByUserName(userName).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с ником %s не найден!"
                        .formatted(userName))
        );

        return UserDto.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUserName())
                .build();
    }
}
