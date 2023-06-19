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
    private ThreadPoolExecutor asyncThreadPool = null;
    /** 定时任务线程池 */
    private ThreadPoolExecutor schedulerThreadPool = null;

    /** 掩码 */
    private int mark;

    /** 关闭 */
    private volatile boolean shutdown = false;

    public boolean init(int coreThreadNum, int asyncThreadNum) {
        int coreThreadSize = coreThreadNum;
        if ((coreThreadSize & (coreThreadSize - 1)) != 0 || coreThreadSize < 8 || coreThreadSize > 256) {
            throw new RuntimeException("核心线程数必须是2的N次方，并且在[8, 256]范围内" + coreThreadNum);
        }
        mark = coreThreadSize - 1;

        coreThreadPools = new ThreadPoolExecutor[coreThreadSize];
        DefaultThreadFactory threadPoolsFactory1 = new DefaultThreadFactory("CoreThreadPools", coreThreadSize);
        for (int i = 0; i < coreThreadPools.length; i++) {
            coreThreadPools[i] = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadPoolsFactory1);
        }
        log.info("init core Thread pool, size:{}", coreThreadSize);

        // 异步线程池
        int asyncThreadSize = Math.max(2, asyncThreadNum);
        DefaultThreadFactory threadPoolsFactory2 = new DefaultThreadFactory("AsyncThreadPool", asyncThreadSize);
        asyncThreadPool = new ThreadPoolExecutor(2, asyncThreadSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadPoolsFactory2);
        log.info("init async Thread pool, size:{}", asyncThreadSize);

        // 定时任务线程池
        int coreNum = Runtime.getRuntime().availableProcessors();
        int schedulerThreadSize = Math.max(2, coreNum);
        schedulerThreadSize = Math.min(8, schedulerThreadSize);
        DefaultThreadFactory threadPoolsFactory3 = new DefaultThreadFactory("SchedulerThreadPool", schedulerThreadSize);
        schedulerThreadPool = new ThreadPoolExecutor(2, schedulerThreadSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadPoolsFactory3);
        log.info("init scheduler Thread pool, size:{}", schedulerThreadSize);

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

    public ThreadPoolExecutor getSchedulerThreadPool() {
        return schedulerThreadPool;
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
        return asyncThreadPool.submit(runnable);
    }

}
