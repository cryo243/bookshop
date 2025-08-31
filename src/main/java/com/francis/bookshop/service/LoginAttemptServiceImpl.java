package com.francis.bookshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    @Value("${max.login.attempt}")
    private int MAX_LOGIN_ATTEMPTS;

    @Value("${login.window.ms}")
    private int LOGIN_WINDOW_MS;

    private final Map<String, Deque<Long>> attempts = new ConcurrentHashMap<>();
    @Override
    public void recordFailure(String key) {
        var queue = attempts.computeIfAbsent(key, k -> new ArrayDeque<>());
        long now = System.currentTimeMillis();
        queue.addLast(now);
        while (!queue.isEmpty() && now - queue.peekFirst() > LOGIN_WINDOW_MS) queue.removeFirst();
    }

    @Override
    public void recordSuccess(String key) {
        attempts.remove(key);
    }

    @Override
    public boolean isBlocked(String key) {
        var queue = attempts.get(key);
        return !isNull(queue) && queue.size() >= MAX_LOGIN_ATTEMPTS;
    }
}
