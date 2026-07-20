package com.supermap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gzw
 */
public class AtomicTest {

    private static final AtomicInteger stock = new AtomicInteger(10);

    private static final int threadCount = 16;
    private static final ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 2; i++) {
                executor.execute(() -> {
                    System.out.println("start stock: " + stock.get());
                    while (stock.get() > 0) {
                        stock.compareAndExchange(stock.get(), stock.get() - 1);
                    }
                    System.out.println("end stock: " + stock.get());
                });
            }
        } finally {
            executor.shutdown();
        }

    }

}
