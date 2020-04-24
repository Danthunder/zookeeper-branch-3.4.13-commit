package com.zookeeper.client.distribute.config;

import java.util.concurrent.TimeUnit;

/**
 * @author Wang danning
 * @since 2020-03-06 22:47
 **/
public class TestMain {
    public static void main(String[] args) throws InterruptedException {
        Config config = new Config();

        config.save("timeout", "1000");

        for (int i = 0; i < 100; i++) {

            System.out.println(config.get("timeout"));

            TimeUnit.SECONDS.sleep(5);
        }
    }
}
