package com.nearinfinity.examples.zookeeper.confservice;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.nearinfinity.examples.zookeeper.util.MoreZKPaths;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigUpdater.class);

    public static final String PATH = MoreZKPaths.makeAbsolutePath("config");

    private final ActiveKeyValueStore store;
    private final Random random = new Random();

    public ConfigUpdater(String hosts) throws IOException, InterruptedException {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }

    @SuppressWarnings("squid:S2189")
    public void run() throws InterruptedException, KeeperException {
        //noinspection InfiniteLoopStatement
//        while (true) {
//            int value = random.nextInt(100);
//            store.write(PATH, Integer.toString(value));
//            LOG.info("Set {} to {}", PATH, value);
//            TimeUnit.SECONDS.sleep(random.nextInt(10));
//        }

        store.write(PATH + "/" + "first", "1");
        store.write(PATH + "/" + "second", "2");

        TimeUnit.SECONDS.sleep(10);

        store.zooKeeper().delete(PATH + "/" + "first", 0);
        store.zooKeeper().delete(PATH + "/" + "second", 0);
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String host = "localhost:2181";
        ConfigUpdater updater = new ConfigUpdater(host);
        updater.run();
    }

}
