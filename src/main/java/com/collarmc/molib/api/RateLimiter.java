package com.collarmc.molib.api;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Service for actions that interact with Mojang servers that are rate limited.
 * Typically Mojang APIs are rate limited at 1 QPS
 */
public class RateLimiter {
    private static final int COOLDOWN_SEC = 1; // min time between mojang service calls
    private final Semaphore semaphore = new Semaphore(1);
    private volatile long lastCoolDown = 0;

    public RateLimiter() {}

    /**
     * Do something with the Mojang service. Blocks until Semaphore can be acquired.
     * @param callable to execute
     */
    public <T> Optional<T> perform(Callable<Optional<T>> callable) {
        semaphore.acquireUninterruptibly();
        // Run cooldown first so that we can release the semaphore earlier
        // This means we can release threads back to executors sooner
        coolDown();
        try {
            return callable.call();
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            semaphore.release();
        }
    }

    private void coolDown() {
        // If our operation took longer than the cooldown to perform last time
        // then don't bother with a cooldown
        if ((lastCoolDown + (COOLDOWN_SEC * 1000)) >= System.currentTimeMillis()) {
            return;
        }
        lastCoolDown = System.currentTimeMillis();
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(COOLDOWN_SEC));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
