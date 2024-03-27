package org.example;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class KeyThreadPool {
    private final Map<Object, Queue<Runnable>> tasks = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor executor;
    private final ReentrantLock lock = new ReentrantLock();

    public KeyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public void execute(Object key, Runnable task) {
        Queue<Runnable> keyTasks = tasks.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());
        boolean firstTask = false;

        lock.lock();
        try {
            if (keyTasks.isEmpty()) {
                firstTask = true;
            }
            keyTasks.offer(() -> {
                try {
                    task.run();
                } finally {
                    scheduleNext(key);
                }
            });
        } finally {
            lock.unlock();
        }

        if (firstTask) {
            executor.execute(keyTasks.peek());
        }
    }

    private void scheduleNext(Object key) {
        lock.lock();
        try {
            Queue<Runnable> keyTasks = tasks.get(key);
            if (keyTasks != null) {
                keyTasks.poll();
                if (!keyTasks.isEmpty()) {
                    executor.execute(keyTasks.peek());
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
