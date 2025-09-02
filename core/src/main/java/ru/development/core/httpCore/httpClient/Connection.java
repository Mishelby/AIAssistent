package ru.development.core.httpCore.httpClient;

import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Value
@Accessors(fluent = true)
public class Connection {
   HttpClientConnection httpConnection;

    public Connection(HttpClientConnection httpConnection) {
        this.httpConnection = httpConnection;
    }
}
