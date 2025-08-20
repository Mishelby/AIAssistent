package ru.development.infrastructurekafka.enums;

import lombok.Getter;

@Getter
public enum KafkaTopic {
    GIGACHAT_MESSAGE("gigachat.message", 3, 1),
    GIGACHAT_INFO("gigachat.info", 3, 1),
    CHECK_TOKEN_INFO("token.info", 3, 1),
    MATH_RESULT("math.result", 3, 1),;

    private final String name;
    private final int partition;
    private final short replication;

    KafkaTopic(String name, int partition, int replication) {
        this.name = name;
        this.partition = partition;
        this.replication = (short) replication;
    }
}
