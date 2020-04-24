package com.zookeeper.client.distribute.lock;

/**
 * @author Wang danning
 * @since 2020-03-03 21:20
 **/
public class Stock {

    private static Integer COUNT = 1;

    public boolean reduceStock() {
        if (COUNT > 0) {
            COUNT --;
            System.out.println(Thread.currentThread().getName() + "：减库存成功");
            return true;
        }
        System.out.println(Thread.currentThread().getName() + "：减库存失败");
        return false;
    }
}
