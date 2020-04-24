package com.zookeeper.client.distribute.lock;

/**
 * @author Wang danning
 * @since 2020-03-03 21:19
 **/
public class Order {

    public void createOrder(){
        System.out.println(Thread.currentThread().getName() + ": createOrder");
    }

}
