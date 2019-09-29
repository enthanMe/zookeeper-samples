package com.nearinfinity.examples.zookeeper.confservice;

import static java.net.URLDecoder.decode;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigWatcher implements Watcher {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigWatcher.class);

    private final ActiveKeyValueStore store;

    public ConfigWatcher(String hosts) throws InterruptedException, IOException {
        store = new ActiveKeyValueStore();
        store.connect(hosts);
    }

    @Override
    public void process(WatchedEvent event) {
        LOG.info("process(event={})", event);

        if (event.getType() == Event.EventType.NodeDataChanged) {
            try {
                String value = store.read(ConfigUpdater.PATH, this);
                LOG.info("type={}, path={}, value={}", EventType.NodeDataChanged, ConfigUpdater.PATH, value);
            } catch (InterruptedException e) {
                LOG.error("Interrupted. Exiting", e);
                Thread.currentThread().interrupt();
            } catch (KeeperException e) {
                LOG.error("KeeperException: {}", e.code(), e);
            }
        }

        if (event.getType() == EventType.NodeChildrenChanged) {
            try {
                store.zooKeeper().getChildren(ConfigUpdater.PATH, this).forEach(child -> {
                    try {
                        LOG.info("type={}, path={}, value={}", EventType.NodeChildrenChanged, decode(child, "UTF-8"), store.read(ConfigUpdater.PATH + "/" + child, this));
                    } catch (UnsupportedEncodingException | InterruptedException | KeeperException e) {
                        e.printStackTrace();
                    }
                });
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        String host = "localhost:2181";
        ConfigWatcher watcher = new ConfigWatcher(host);

        watcher.store.read(ConfigUpdater.PATH, watcher);

        watcher.store.zooKeeper().getChildren(ConfigUpdater.PATH, watcher).forEach(child -> {
            try {
                LOG.info("path={}, value={}", decode(child, "UTF-8"), watcher.store.read(ConfigUpdater.PATH + "/" + child, watcher));
            } catch (UnsupportedEncodingException | InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(Long.MAX_VALUE);
    }

}
