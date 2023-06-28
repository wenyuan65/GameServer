package com.panda.game.core.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.panda.game.common.constants.NodeCluster;
import com.panda.game.common.constants.NodeType;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;

import java.util.*;

/**
 * 名称服务功能<br/>
 * 各个名称在游戏框架中的应用：<br/>
 * serviceName：对应游戏名称，例如王者荣耀游戏简称，wzry<br/>
 * groupName:   对应服务器类型，例如gateway/game/chat/name/club/data/friend等等<br/>
 * clusterName: 对应服务器分区类型，例如用common表示公共服，用xianyu/37/苹果/37安卓等等表示不同渠道独服或者混服<br/>
 * 通过使用这三个名称，将服务名称形成一棵4层的目录树，方便检索与使用
 * 应用:      用于标识服务提供方的服务的属性。
 * 服务分组： 不同的服务可以归类到同一分组。
 * 虚拟集群： 同一个服务下的所有服务实例组成一个默认集群, 集群可以被进一步按需求划分，划分的单位可以是虚拟集群。
 * 实例：    提供一个或多个服务的具有可访问网络地址（IP:Port）的进程。
 */
public class NodeManager implements EventListener {

    public static final Logger log = LoggerFactory.getLogger(NodeManager.class);

    private static final NodeManager instance = new NodeManager();

    private NodeManager() {
    }

    public static NodeManager getInstance() {
        return instance;
    }

    private NamingService namingService;

    private String game;

    private Map<String, Instance> instanceMap = null;

    /**
     * 初始化名称服务，建立链接与监听
     * @param game
     * @param connections  "127.0.0.1:8848" 集群使用逗号分割
     */
    public boolean init(String connections, String game) {
        try {
            this.game = game;
            this.namingService = NamingFactory.createNamingService(connections);

            this.namingService.subscribe(this.game, this);
        } catch (NacosException e) {
            log.error("#node#init,{}", e, connections);
            return false;
        }

        return true;
    }

    /**
     * 批量注册实例
     * @param instances
     */
    public void register(NodeType nodeType, List<Instance> instances) {
        try {
            namingService.batchRegisterInstance(this.game, nodeType.getName(), instances);
        } catch (NacosException e) {
            log.error("#node#register", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 随机选择一个实例
     * @param nodeType 服务器类型
     * @return
     */
    public Instance selectNode(NodeType nodeType) {
        return selectNode(nodeType, NodeCluster.Common);
    }

    /**
     * 随机选择一个实例
     * @param nodeType 服务器类型
     * @param nodeCluster 服务器渠道
     * @return
     */
    public Instance selectNode(NodeType nodeType, NodeCluster nodeCluster) {
        try {
            return namingService.selectOneHealthyInstance(this.game, nodeType.getName(), Arrays.asList(nodeCluster.getName()), true);
        } catch (NacosException e) {
            log.error("#node#selectOne", e);
        }

        return null;
    }

    /**
     * 获取指定实例对象,
     * @return
     */
    public Instance getNode(NodeType nodeType, int id) {
        String instanceId = NodeHelper.getInstanceId(nodeType.getName(), id);

        try {
            if (instanceMap == null) {
                log.info("#node#instance map is null");

                Map<String, Instance> instanceMap2 = new HashMap<>();
                List<Instance> list = namingService.getAllInstances(this.game, true);
                for (Instance instance : list) {
                    instanceMap2.put(instance.getInstanceId(), instance);
                }

                if (instanceMap == null) {
                    instanceMap = instanceMap2;
                }
            }

            return instanceMap.get(instanceId);
        } catch (NacosException e) {
            log.error("#node#getInstance", e);
        }
        return null;
    }

    @Override
    public void onEvent(Event event) {
        Map<String, Instance> instanceMap2 = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        List<Instance> instances = ((NamingEvent) event).getInstances();
        for (Instance instance : instances) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(instance.getInstanceId());

            instanceMap2.put(instance.getInstanceId(), instance);
        }
        this.instanceMap = instanceMap2;

        log.info("#node#instances#[{}]", sb.toString());
    }

}
