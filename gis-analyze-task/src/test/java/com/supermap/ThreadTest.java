package com.supermap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author gzw
 */
public class ThreadTest {

    public static void main(String[] args) throws Exception {
        TaskRunnable taskRunnable = new TaskRunnable();
        TaskCallable taskCallable = new TaskCallable();

        // Runnable
//        Thread thread = new Thread(taskRunnable);
//        thread.start();

        // Callable
//        FutureTask<Object> futureTask = new FutureTask<>(taskCallable);
//        Thread thread = new Thread(futureTask);
//        thread.start();
//        Object o1 = null;
//        try {
//            o1 = futureTask.get();
//        } catch (InterruptedException | ExecutionException e) {
//            System.out.println("出现了异常");
//        }
//        System.out.println(o1);

        // thread pool
        ExecutorService executor = Executors.newFixedThreadPool(5);

        try {
            executor.execute(taskRunnable);
            Future<?> future = executor.submit(taskCallable);
            Object o = future.get();
            System.out.println(o);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

        Map<String, Object> map = new HashMap<>();
    }

    public static class TaskRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println("TaskRunnable start ...");
            throw new RuntimeException("TaskRunnable error");
        }

    }

    public static class TaskCallable implements Callable<Object> {

        @Override
        public Object call() {
            System.out.println("TaskCallable start ...");
            int i = 1 / 0;
            return "result";
        }

    }

}
