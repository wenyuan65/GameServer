package com.panda.game.core.cache;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.ScanUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CacheFactory {

    private static final Logger logger = LoggerFactory.getLogger(CacheFactory.class);

    private static Map<Class<? extends Cache<?>>, Cache<?>> cacheMap = new HashMap<>();

    public static boolean init(String pkg) {
        Set<Class<?>> classSet = ScanUtil.scan(pkg);
        for (Class<?> clazz : classSet) {
            if (!Cache.class.isAssignableFrom(clazz)) {
                continue;
            }
            int modifiers = clazz.getModifiers();
            if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
                continue;
            }

            try {
                Class<? extends Cache<?>> cacheClazz = (Class<? extends Cache<?>>) clazz;
                Constructor<? extends Cache<?>> constructor = cacheClazz.getConstructor();
                Cache<?> cache = constructor.newInstance();
                cacheMap.put(cacheClazz, cache);
            } catch (Throwable e) {
                logger.error("init cache factory error", e);
                return false;
            }
        }

        return true;
    }

    /**
     * 重新加载所有数据
     * @return
     */
    public static boolean reloadAll() {
        for (Map.Entry<Class<? extends Cache<?>>, Cache<?>> entry : cacheMap.entrySet()) {
            Cache<?> cache = entry.getValue();
            try {
                if (cache.reload()) {
                    return false;
                }
            } catch (Throwable e) {
                logger.error("reload cache error", e);
                return false;
            }
        }

        return true;
    }

    public static <T extends Cache<?>> T getCache(Class<T> clazz) {
        return (T) cacheMap.get(clazz);
    }

}
