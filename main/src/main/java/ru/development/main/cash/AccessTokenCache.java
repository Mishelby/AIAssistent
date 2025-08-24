package ru.development.main.cash;

import ru.development.main.model.AccessToken;


public interface AccessTokenCache {
    AccessToken get(String key);
    void put(String key, AccessToken accessToken);
    void remove(String key);
    boolean containsKey(String key);
    String getFullInfo();
}
