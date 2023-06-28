package com.panda.game.core.nacos;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.game.common.utils.StringUtils;

public class NodeHelper {

    /**
     * 注册实例
     * @param nodeType
     * @param nodeId
     * @param ip
     * @param port
     * @return
     */
    public static Instance createInstance(String cluster, String nodeType, int nodeId, String ip, int port) {
        String instanceId = getInstanceId(nodeType, nodeId);

        Instance instance = new Instance();
        instance.setClusterName(cluster);
        instance.setInstanceId(instanceId);
        instance.setIp(ip);
        instance.setPort(port);

        return instance;
    }

    /**
     * 获取实例id
     * @param nodeType
     * @param id
     * @return
     */
    public static String getInstanceId(String nodeType, int id) {
        return nodeType + "_" + id;
    }

    /**
     * 通过实例id获取nodeId
     * @param instanceId
     * @return
     */
    public static int getNodeId(String instanceId) {
        String[] arrays = StringUtils.split(instanceId, "_");
        return Integer.parseInt(arrays[1]);
    }

}
