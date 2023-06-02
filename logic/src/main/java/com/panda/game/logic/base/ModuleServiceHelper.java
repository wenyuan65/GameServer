package com.panda.game.logic.base;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.ScanUtil;
import com.panda.game.logic.LogicServer;
import com.panda.game.logic.annotation.Group;
import com.panda.game.logic.common.ModuleGroups;

import java.lang.reflect.Modifier;
import java.util.*;

public class ModuleServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ModuleService.class);

    private static final List<Class<? extends ModuleService>> ServiceList = new ArrayList<>();
    private static final Map<ModuleGroups, List<Class<? extends ModuleService>>> BlockServicesMap = new HashMap<>();

    public static boolean init() {
        Set<Class<?>> classes = ScanUtil.scan(LogicServer.class.getPackage().getName());
        for (Class<?> clazz : classes) {
            if (!ModuleService.class.isAssignableFrom(clazz)) {
                continue;
            }

            int modifiers = clazz.getModifiers();
            if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
                continue;
            }
            Class<? extends ModuleService> serviceClazz = (Class<? extends ModuleService>)clazz;
            ServiceList.add(serviceClazz);

            logger.info("初始化{}", clazz.getName());

            Group group = clazz.getDeclaredAnnotation(Group.class);
            if (group != null) {
                BlockServicesMap.computeIfAbsent(group.value(), k -> new ArrayList<>()).add(serviceClazz);
            } else {
                BlockServicesMap.computeIfAbsent(ModuleGroups.Other, k -> new ArrayList<>()).add(serviceClazz);
            }
        }

        return true;
    }

    public static List<Class<? extends ModuleService>> getAllServices() {
        return Collections.unmodifiableList(ServiceList);
    }

    /**
     * 获取指定的
     * @param blockList
     * @return
     */
    public static List<Class<? extends ModuleService>> getModuleGroupServices(List<ModuleGroups> blockList) {
        List<Class<? extends ModuleService>> result = new ArrayList<>();
        // 获取模块类
        List<Class<? extends ModuleService>> baseClassList = BlockServicesMap.get(ModuleGroups.Base);
        result.addAll(baseClassList);

        Set<ModuleGroups> loadedBlocks = new HashSet<>();
        loadedBlocks.add(ModuleGroups.Base);
        for (ModuleGroups block : blockList) {
            if (loadedBlocks.contains(block)) {
                continue;
            }

            List<Class<? extends ModuleService>> list = BlockServicesMap.get(block);
            result.addAll(list);
            loadedBlocks.add(block);
        }

        return result;
    }

}
