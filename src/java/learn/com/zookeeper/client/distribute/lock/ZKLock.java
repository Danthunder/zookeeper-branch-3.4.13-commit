package com.zookeeper.client.distribute.lock;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Wang danning
 * @since 2020-03-03 21:56
 **/
public class ZKLock implements Lock {

    private ThreadLocal<ZooKeeper> zk = new ThreadLocal<>();

    CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181",
            new RetryNTimes(3, 1000));

    InterProcessMutex interProcessMutex = new InterProcessMutex(client,"/anyLock");

    private String LOCK_NAME = "/lock";

    private ThreadLocal<String> CURRENT_NODENAME = new ThreadLocal<>();

    public void init(){
        if (zk.get() == null) {
            try {
                zk.set(new ZooKeeper("localhost:2181", 300, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        //
                    }
                }));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void lock(){
        init();
        if (tryLock()) {
            System.out.println(Thread.currentThread().getName() + "：已经获取到锁！");
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    public void unLock(){
        // zookeeper删除节点时需要指定一个版本号，如果版本号错误，删除节点失败
        // 这里用-1代表，强制删除节点，忽略版本
        try {
            System.out.println(Thread.currentThread().getName() + "释放锁");
            zk.get().delete(CURRENT_NODENAME.get(), -1);
            CURRENT_NODENAME.set(null);
            zk.get().close();

        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    public boolean tryLock() {
        // 创建节点
        String nodeName = LOCK_NAME + "/zk_";

        try {
            CURRENT_NODENAME.set(zk.get().create(nodeName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));

            // list中保存是 zk1, zk2, ...
            List<String> list = zk.get().getChildren(LOCK_NAME, false);

            // sort list
            Collections.sort(list);

            String minNodeName = list.get(0);

            if(CURRENT_NODENAME.get().equals(LOCK_NAME + "/" + minNodeName)) {
                return true;
            } else {

                int currentIndex = list.indexOf(CURRENT_NODENAME.get().substring(CURRENT_NODENAME.get().lastIndexOf("/") + 1));

                String prevNodeName = LOCK_NAME + "/" + list.get(currentIndex-1);

                // 需要阻塞
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                zk.get().exists(prevNodeName, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (Event.EventType.NodeDeleted.equals(event.getType())) {
                            countDownLatch.countDown();
                        }
                    }
                });
                // 等待前一个节点删除后，放行，返回true
                System.out.println(Thread.currentThread().getName() + "线程，等待锁阻塞中。。。");
                countDownLatch.await();
                return true;
            }

        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
