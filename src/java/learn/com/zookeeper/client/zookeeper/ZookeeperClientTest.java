package com.zookeeper.client.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * @author Wang danning
 * @since 2020-02-21 21:33
 **/
public class ZookeeperClientTest {
    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        // 默认Watch
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181",
                5000,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        System.out.println("连接时执行" + event);
                    }
                });

        Stat stat = new Stat();
        String currentNodePath = "/wdn";
//        String str = new String(zooKeeper.getData(currentNodePath,
//                new Watcher() {
//                    @Override
//                    public void process(WatchedEvent event) {
//                        if(Event.EventType.NodeDataChanged.equals(event.getType())) {
//                            System.out.println("当前节点[" + event.getPath() + "]的数据发生改变!");
//                        }
//                    }
//                },
//                stat));

//        String str = new String(zooKeeper.getData(currentNodePath,
//                true,
//                stat));
//        System.out.println("getData:" + str);

        zooKeeper.getData(currentNodePath,
                new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (Event.EventType.NodeDataChanged.equals(event.getType())) {
                            System.out.println("当前节点[" + event.getPath() + "]的数据发生改变!");
                        }
                    }
                },
                new AsyncCallback.DataCallback() {
                    /**
                     *
                     * @param rc   The return code or the result of the call.
                     * @param path The path that we passed to asynchronous calls.
                     * @param ctx  Whatever context object that we passed to
                     *             asynchronous calls.
                     * @param data The {@link org.apache.zookeeper.server.DataNode#data}
                     *             of the node.
                     * @param stat {@link org.apache.zookeeper.data.Stat} object of
                     */
                    @Override
                    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                        System.out.println("rc:" + rc + "\n" +
                                "path:" + path + "\n" +
                                "ctx:" + ctx +
                                "data:" + new String(data) + "\n" +
                                "stat:" + stat);
                    }
                },
                stat);

        zooKeeper.create("/opt",
                "123".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);

        System.in.read();
    }
}
