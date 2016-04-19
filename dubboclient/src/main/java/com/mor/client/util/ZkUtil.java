package com.mor.client.util;

import com.google.common.collect.Lists;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 
 * @author wanggengqi
 * @email wanggengqi@chinasofti.com
 * @date 2016年4月18日 下午2:40:57
 */
public class ZkUtil {
    private static final Logger log = LoggerFactory.getLogger(ZkUtil.class);

    public static CuratorFramework curatorFramework = null;
    public static String path = null;

    private static volatile String zkHosts = null;
    private static volatile String zkPath = null;
    private static volatile Lock lock = new ReentrantLock();

    public static void init(String zkHosts, String zkPath) {
        try {
            lock.lock();
            if (ZkUtil.zkHosts == null) {
                ZkUtil.zkHosts = zkHosts;
            }
            if (ZkUtil.zkPath == null) {
                ZkUtil.zkPath = zkPath;
                path = zkPath;
            }
            if (curatorFramework != null) {
                return;
            }
            RetryPolicy rp = new ExponentialBackoffRetry(1000, 10);
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
            builder.connectString(ZkUtil.zkHosts).connectionTimeoutMs(5000);
            builder.sessionTimeoutMs(10000);
            builder.retryPolicy(rp);
            curatorFramework = builder.build();
            curatorFramework.start();
            try {
                curatorFramework.blockUntilConnected();
                curatorFramework.createContainers(path);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } finally {
            lock.unlock();
        }
    }




    private static InetAddress getAddress() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
                    .hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                if (addresses.hasMoreElements()) {
                    return addresses.nextElement();
                }
            }
        } catch (SocketException e) {
            log.error("Error when getting host ip address:", e);
            throw new RuntimeException("获取ip出错", e);
        }
        return null;
    }

    public static String register(String ip, String port) {
        String addr = ip + ":" + port;
        log.info("register:{} to zookeeper", addr);
        try {
            return ZkUtil.createEphemeralSequential(path, addr.getBytes("utf-8"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static String createEphemeralSequential(String path, byte[] payload) throws Exception {
        return curatorFramework.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path + "/serve", payload);
    }


    public static boolean registerNodeId(String nodeId, String ip, String port) {
        String addr = ip + ":" + port;
        log.info("register:nodeId:{} @ {} to zookeeper", nodeId, addr);

        try {
            if (exist(nodeId)) {
                log.error("zookeeper上已经存在nodeId:{}", nodeId);
                return false;
            }
            String ret = createEphemeral("/node", nodeId, addr.getBytes("utf-8"));
            if (ret != null) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public static String createEphemeral(String containPath, String nodeId, byte[] payload) throws Exception {
        curatorFramework.createContainers(containPath);
        return curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(containPath + "/" + nodeId, payload);
    }


    public static boolean exist(String nodeId) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath("/node" + "/" + nodeId);
        return stat != null;
    }

    public static List<String> activeNode() throws Exception {
        Stat stat = curatorFramework.checkExists().forPath("/node");
        if (stat == null) {
            return Collections.emptyList();
        }

        List<String> nodeList = curatorFramework.getChildren().forPath("/node");
        List<String> dataList = Lists.newArrayList();
        for (String node : nodeList){
            byte[] data = curatorFramework.getData().forPath("/node/" + node);
            dataList.add(new String(data));
        }

        return dataList;

    }


}
