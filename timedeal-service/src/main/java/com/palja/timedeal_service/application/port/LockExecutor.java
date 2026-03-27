package com.palja.timedeal_service.application.port;

import java.util.concurrent.TimeUnit;

public interface LockExecutor {
    void execute(String key, long waitTime, TimeUnit timeUnit, Runnable task);
}
