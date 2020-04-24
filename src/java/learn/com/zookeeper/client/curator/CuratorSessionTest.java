package com.zookeeper.client.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;

/**
 * @author Wang danning
 * @since 2020-02-22 12:37
 **/
public class CuratorSessionTest {

    public static void main(String[] args) {
        final CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181",new RetryNTimes(3,1000));

        client.start();

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState == ConnectionState.LOST) {
                    try {
                        if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                            doTask();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        doTask();
    }


    public static void doTask() {
        System.out.println("执行相关任务");
    }
}
