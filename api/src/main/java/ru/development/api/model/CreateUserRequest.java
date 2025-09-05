package ru.development.api.model;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link UserEntity}
 */
@Value
@Builder
public class CreateUserRequest implements Serializable {
    String firstName;
    String lastName;
    String userName;
    String chatNumber;
}
