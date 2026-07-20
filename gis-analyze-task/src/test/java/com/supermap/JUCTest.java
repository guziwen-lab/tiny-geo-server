package com.supermap;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gzw
 */
@Slf4j
public class JUCTest {

    private static final int threadCount = 16;
    private static final int limit = 4;

    private static final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    public static void main(String[] args) throws Exception {
        semaphoreTest();
    }

    /**
     * 一共threadCount线程，每次允许limit个线程同时执行
     */
    private static void semaphoreTest() {
        Semaphore semaphore = new Semaphore(limit);

        try {
            for (int i = 0; i < threadCount; i++) {
                executor.execute(() -> {
                    boolean acquired = false;
                    try {
                        semaphore.acquire();
                        acquired = true;
                        analyze();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        if (acquired)
                            semaphore.release();
                    }
                });

            }
        } finally {
            executor.shutdown();
        }
    }

    private static final AtomicInteger running = new AtomicInteger();

    private static void analyze() {
        int current = running.incrementAndGet();
        System.out.println(Thread.currentThread().getName() + " 开始，当前运行数：" + current);
        System.out.println("执行分析任务");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        running.decrementAndGet();
        System.out.println(Thread.currentThread().getName() + " 结束，当前运行数：" + current);
    }

}
