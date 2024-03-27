package org.example;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) {
        KeyThreadPool threadPool = new KeyThreadPool(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        // 示例任务
        Runnable task1 = () -> System.out.println("Task 1 for Key A" + Thread.currentThread());
        Runnable task2 = () -> System.out.println("Task 2 for Key A"+ Thread.currentThread());
        Runnable task3 = () -> System.out.println("Task 1 for Key B"+ Thread.currentThread());
        Runnable task4 = () -> System.out.println("Task 2 for Key B"+ Thread.currentThread());

        // 启动任务
        threadPool.execute("Key A", task1);
        threadPool.execute("Key A", task2);
        threadPool.execute("Key B", task3);
        threadPool.execute("Key B", task4);

        System.out.println("end");
    }
}
