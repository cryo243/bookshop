package com.francis.bookshop.service;

public interface LoginAttemptService {
    void recordFailure(String key);
    void recordSuccess(String key);
    boolean isBlocked(String key);
}
