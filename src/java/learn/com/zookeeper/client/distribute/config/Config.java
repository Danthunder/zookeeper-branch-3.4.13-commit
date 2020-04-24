package com.zookeeper.client.distribute.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wang danning
 * @since 2020-03-06 22:05
 **/
public class Config {

    private Map<String, String> cache = new HashMap<>();

    private CuratorFramework client;

    private static final String CONFIG_PREFIX = "/CONFIG";

    public void init() {
        // 将zk中的所有配置，加载到本地缓存cache中
        try {
            List<String> childrenNames = client.getChildren().forPath(CONFIG_PREFIX);

            for (String childrenName : childrenNames) {

                String childrenFullName = CONFIG_PREFIX + "/" + childrenName;

                String value = new String(client.getData().forPath(childrenFullName));

                cache.put(childrenName, value);

            }

            // 绑定一个监听器，监听CONFIG_PREFIX节点，监听事件包括
            // 孩子节点的新建、删除、更新
            // 第三个参数true的含义为，当事件发生之后，可以获取到发生事件的节点的内容
            PathChildrenCache watch = new PathChildrenCache(client, CONFIG_PREFIX, true);

            watch.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

                    String eventNodeFullPath = event.getData().getPath();

                    if (eventNodeFullPath.startsWith(CONFIG_PREFIX)) {

                        String key = eventNodeFullPath.replace(CONFIG_PREFIX + "/", "");

                        String value = new String(event.getData().getData());

                        if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(event.getType()) ||
                                PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(event.getType())) {

                            cache.put(key, value);

                        } else if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(event.getType())) {

                            cache.remove(key);

                        }
                    }
                }
            });

            watch.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Config() {
        this.client = CuratorFrameworkFactory.newClient("localhost:2181",
                new RetryNTimes(3, 1000));
        this.client.start();
        init();
    }

    public void save(String name, String value) {
        // update zk
        // update cache
        String configFullName = CONFIG_PREFIX + "/" + name;
        try {
            Stat stat = client.checkExists().forPath(configFullName);

            if (stat == null) {
                // 当前配置项不存在，则创建zk节点并将配置内容保存
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(configFullName, value.getBytes());
            } else {
                // 当前配置存在，则更新该zk节点的配置内容
                client.setData().forPath(configFullName, value.getBytes());
            }

            // 更新缓存
            cache.put(name, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String name) {
        // get from cache, watch 机制更新cache
        return cache.get(name);
    }
}
