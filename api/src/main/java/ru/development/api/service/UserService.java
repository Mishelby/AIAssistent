package ru.development.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.development.api.model.ChatEntity;
import ru.development.api.model.CreateUserRequest;
import ru.development.api.model.UserDto;
import ru.development.api.model.UserEntity;
import ru.development.api.repository.UserEntityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserEntityRepository userRepository;

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
}
