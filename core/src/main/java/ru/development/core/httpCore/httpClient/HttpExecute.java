package ru.development.core.httpCore.httpClient;

import java.io.IOException;

public interface HttpExecute<T, R> {
    R execute(T input) throws IOException, InterruptedException;
}
