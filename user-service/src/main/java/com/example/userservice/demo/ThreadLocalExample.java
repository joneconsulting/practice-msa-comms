package com.example.userservice.demo;

public class ThreadLocalExample {
    private static ThreadLocal<Integer> threadLocalValue = new ThreadLocal<>();

    public static void main(String[] args) {
        Runnable task = () -> {
            int value = (int) (Math.random() * 100);
            threadLocalValue.set(value);
            System.out.println(Thread.currentThread().getName() + " => " + threadLocalValue.get());
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        t1.start();
        t2.start();
    }
}