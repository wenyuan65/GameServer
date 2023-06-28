package com.panda.game.core.proto;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.ScanUtil;
import com.panda.game.proto.CmdPb;
import com.panda.game.proto.PacketPb;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProtoManager {

    private static final Logger logger = LoggerFactory.getLogger(ProtoManager.class);

    private static final ProtoManager instance = new ProtoManager();

    private ProtoManager() {
    }

    public static ProtoManager getInstance() {
        return instance;
    }

    // 协议名称对应class
    private final Map<String, Class<?>> protocolMap = new HashMap<>();
    // cmd对应的返回的协议的解析器
    private final Map<Integer, Parser<? extends GeneratedMessageV3>> cmdResponseParserMap = new HashMap<>();

    private boolean inited = false;

    public boolean init() {
        if (inited) {
            return true;
        }

        Set<Class<?>> classes = ScanUtil.scan(PacketPb.class.getPackage().getName());
        for (Class<?> clazz : classes) {
            loadProtocol(clazz);
            parseCmdResponseParser(clazz);
        }

        return true;
    }

    /**
     * 解析rq对应的rs的解析器
     * @param clazz
     */
    private void parseCmdResponseParser(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        if (simpleName.endsWith("Rs")) {
            String rqName = simpleName.substring(0, simpleName.length() - 2) + "Rq";
            CmdPb.Cmd cmd = CmdPb.Cmd.valueOf(rqName);

            try {
                Method method = clazz.getDeclaredMethod("parser");
                Parser<? extends GeneratedMessageV3> parser = (Parser<? extends GeneratedMessageV3>)method.invoke(null);
                cmdResponseParserMap.put(cmd.getNumber(), parser);
            } catch (Throwable e) {
                logger.error("获取协议的解析器异常");
            }
        }
    }

    /**
     * 解析rq/rs/push协议对应的协议类
     * @param clazz
     */
    private void loadProtocol(Class<?> clazz) {
        if (clazz.getName().endsWith("Push") || clazz.getName().endsWith("Rs") || clazz.getName().endsWith("Rq")) {
            protocolMap.put(clazz.getSimpleName(), clazz);
        }
    }

    public Parser<? extends GeneratedMessageV3> getResponseParser(int rqCmd) {
        return cmdResponseParserMap.get(rqCmd);
    }

    public Class<?> getProtoClass(String protoName) {
        return protocolMap.get(protoName);
    }

    public int getRsCmd(int rqCmd) {
        return rqCmd + 1;
    }

}
