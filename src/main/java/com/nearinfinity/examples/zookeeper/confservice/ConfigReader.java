package com.nearinfinity.examples.zookeeper.confservice;

import static java.net.URLDecoder.decode;

import com.nearinfinity.examples.zookeeper.util.MoreZKPaths;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigUpdater.class);

    public static final String PATH = MoreZKPaths.makeAbsolutePath("/dubbo/com.m2c.acm.bridge.facade.BridgeFacade/providers/");

    private final ActiveKeyValueStore store;

    public ConfigReader(String hosts) throws IOException, InterruptedException {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }

    private void read() throws KeeperException, InterruptedException {
        String data = store.read(PATH, null);
        LOG.info("data = {}", data);

        store.zooKeeper().getChildren(PATH, false).forEach(path -> {
            try {
                path = decode(path, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            LOG.info("path = {}", path);
        });

    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String host = "zk.meet2cloud.com.cn:2181";
        ConfigReader reader = new ConfigReader(host);
        reader.read();
    }

}
