package com.panda.game.common.concrrent;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;
import com.panda.game.common.utils.RandomUtils;

import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 统一的一个线程管理类
 */
public class ServerThreadManager {

    protected static Logger log = LoggerFactory.getLogger(ServerThreadManager.class);

    private static final ServerThreadManager instance = new ServerThreadManager();

    private ServerThreadManager() {
    }

    public static ServerThreadManager getInstance() {
        return instance;
    }

    /** 核心线程 */
    private ThreadPoolExecutor[] coreThreadPools = null;
    /** 异步线程 */
    private ThreadPoolExecutor asyncThreadPools = null;
    /** 掩码 */
    private int mark;

    /** 关闭 */
    private volatile boolean shutdown = false;

    public boolean init(int coreThreadNum, int asyncThreadNum) {
        int coreThreadPoolSize = coreThreadNum;
        if ((coreThreadPoolSize & (coreThreadPoolSize - 1)) != 0 || coreThreadPoolSize < 8 || coreThreadPoolSize > 256) {
            throw new RuntimeException("核心线程数必须是2的N次方，并且在[8, 256]范围内" + coreThreadNum);
        }
        mark = coreThreadPoolSize - 1;

        coreThreadPools = new ThreadPoolExecutor[coreThreadPoolSize];
        DefaultThreadFactory coreThreadFactory = new DefaultThreadFactory("CoreThreadPools", coreThreadPoolSize);
        for (int i = 0; i < coreThreadPools.length; i++) {
            coreThreadPools[i] = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), coreThreadFactory);
        }
        log.info("init core Thread pool, size:{}", coreThreadPoolSize);

        // 异步线程池
        int poolSize = Math.max(2, asyncThreadNum);
        DefaultThreadFactory asyncThreadPoolsFactory = new DefaultThreadFactory("AsyncThreadPools", poolSize);
        asyncThreadPools = new ThreadPoolExecutor(2, poolSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), asyncThreadPoolsFactory);
        log.info("init async Thread pool, size:{}", poolSize);

        return true;
    }

    public ScheduledThread newScheduleThread(String name, Runnable task, boolean runOnce) {
        return new ScheduledThread(name, task, runOnce, 0);
    }

    public ScheduledThread newScheduleThread(String name, Runnable task, boolean runOnce, long executeTime) {
        return new ScheduledThread(name, task, runOnce, executeTime);
    }

    public ScheduledThread newScheduleThread(String name, Runnable task, long interval) {
        return new ScheduledThread(name, task, 0L, interval);
    }

    public ScheduledThread newScheduleThread(String name, Runnable task, long executeTime, long interval) {
        return new ScheduledThread(name, task, executeTime, interval);
    }

    /**
     * 在随机的线程池中执行任务
     * @param runnable
     * @return
     */
    public Future<?> runAny(Runnable runnable) {
        int index = RandomUtils.nextInt();
        ThreadPoolExecutor threadPool = coreThreadPools[index & mark];
        return threadPool.submit(runnable);
    }

    /**
     * 在指定序号的线程池中执行任务
     * @param runnable
     * @param index
     * @return
     */
    public Future<?> runIn(Runnable runnable, int index) {
        ThreadPoolExecutor threadPool = coreThreadPools[index & mark];
        return threadPool.submit(runnable);
    }

    /**
     * 在几个字段关联的线程池中，执行任务
     * @param runnable
     * @param bindValues
     * @return
     */
    public Future<?> runBy(Runnable runnable, Object... bindValues) {
        int index = Objects.hash(bindValues);
        ThreadPoolExecutor threadPool = coreThreadPools[index & mark];
        return threadPool.submit(runnable);
    }

    /**
     * 执行异步任务
     * @param runnable
     */
    public Future<?> runAsync(Runnable runnable) {
        return asyncThreadPools.submit(runnable);
    }

}
