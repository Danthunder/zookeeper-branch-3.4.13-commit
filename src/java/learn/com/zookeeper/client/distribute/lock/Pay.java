package com.zookeeper.client.distribute.lock;

/**
 * @author Wang danning
 * @since 2020-03-03 21:23
 **/
public class Pay {

    public void pay(){
        System.out.println(Thread.currentThread().getName() + "：支付成功");
    }
}
