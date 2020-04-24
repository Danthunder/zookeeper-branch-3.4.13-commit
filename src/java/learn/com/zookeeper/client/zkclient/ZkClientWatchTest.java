package com.zookeeper.client.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * @author Wang danning
 * @since 2020-02-21 23:24
 **/
public class ZkClientWatchTest {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("localhost:2181",
                10000,
                10000,
                new SerializableSerializer());

        zkClient.writeData("/data", 999);
    }
}
