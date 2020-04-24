package com.zookeeper.client.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.IOException;

/**
 * @author Wang danning
 * @since 2020-02-21 23:05
 **/
public class ZkClientTest {

    public static void main(String[] args) throws IOException {
        ZkClient zkClient = new ZkClient("localhost:2181",
                1000,
                1000,
                new SerializableSerializer());

        zkClient.createPersistent("/data","dd".getBytes());

        zkClient.subscribeDataChanges("/data", new IZkDataListener() {
            /**
             *
             * @param s：当前节点路径
             * @param o：当前节点内容
             * @throws Exception
             */
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("当前节点数据发生改变:" + s + ";" + o);
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        });

        System.in.read();

    }
}
