package com.zookeeper.client.distribute.lock;

/**
 * @author Wang danning
 * @since 2020-03-03 21:23
 **/
public class Test {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new UserThread(), "user1");
        Thread thread2 = new Thread(new UserThread(), "user2");

        thread1.start();
        thread2.start();
    }

    private static ZKLock lock = new ZKLock();

    static class UserThread implements Runnable {

        @Override
        public void run() {
            new Order().createOrder();
            lock.lock();
            boolean result = new Stock().reduceStock();
            lock.unLock();
            if (result) {
                new Pay().pay();
            }
        }
    }
}
