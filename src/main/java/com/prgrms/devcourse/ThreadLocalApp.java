package com.prgrms.devcourse;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.runAsync;

public class ThreadLocalApp {

    final static ThreadLocal<Integer> threadLocalValue = new ThreadLocal<>();

    public static void main(String[] args) {
        System.out.println(getCurrentThreadName() + " ### main set value = 1");
        threadLocalValue.set(1);

        a();
        b();

        // 다른 thread 에서 메소드가 실행되게 하는 간단한 비동기 실행 코드
        CompletableFuture<Void> task = runAsync(() -> {
            //밑 메소드들은 위의 메소드들과 다른 thread에서 실행된다.
            a();
            b();
        });

        task.join();
    }

    public static void a() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + " ### a() get value = " + value);
    }

    public static void b() {
        Integer value = threadLocalValue.get();
        System.out.println(getCurrentThreadName() + " ### b() get value = " + value);
    }

    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }
}
