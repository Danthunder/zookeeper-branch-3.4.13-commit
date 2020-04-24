package com.zookeeper.client.curator;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Wang danning
 * @since 2020-02-22 13:24
 **/
public class LeaderSelecterTest {

    public static void main(String[] args) throws IOException {
        List<CuratorFramework> clients = Lists.newArrayList();

        List<LeaderSelector> leaderSelectors = Lists.newArrayList();

        for (int i = 0; i < 10; i++) {
            CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181",
                    new RetryNTimes(1,1000));
            clients.add(client);
            client.start();

            LeaderSelector leaderSelector = new LeaderSelector(client,
                    "/LeaderSelect",
                    new LeaderSelectorListener() {
                        @Override
                        public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                            // 当上leader后会进入该方法
                            System.out.println("当前Leader是：" + curatorFramework);

                            TimeUnit.SECONDS.sleep(5);
                        }

                        @Override
                        public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {

                        }
                    });
            leaderSelectors.add(leaderSelector);
            leaderSelector.start();
        }

        System.in.read();

        for (CuratorFramework client : clients) {
            client.close();
        }
        for (LeaderSelector leaderSelector : leaderSelectors) {
            leaderSelector.close();
        }


    }
}
