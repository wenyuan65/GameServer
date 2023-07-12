package com.panda.game.common.concrrent;

import com.panda.game.common.concrrent.queue.TaskQueue;
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

    // 任务队列
    private TaskQueue[] queues = new TaskQueue[1024];

    /** 核心线程 */
    private ThreadPoolExecutor coreThreadPools = null;
    /** 异步线程 */
    private ThreadPoolExecutor asyncThreadPool = null;
    /** 定时任务线程池 */
    private ThreadPoolExecutor schedulerThreadPool = null;

    /** 掩码 */
    private int mark = queues.length - 1;

    /** 关闭 */
    private volatile boolean shutdown = false;

    public boolean init(int coreThreadNum, int asyncThreadNum) {
        int coreThreadSize = Math.max(8, coreThreadNum);
        // 核心线程池
        DefaultThreadFactory threadPoolsFactory1 = new DefaultThreadFactory("CoreThreadPools", coreThreadSize);
        coreThreadPools = new ThreadPoolExecutor(coreThreadSize, coreThreadSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadPoolsFactory1);
        log.info("init core Thread pool, size:{}", coreThreadSize);
        // 任务队列
        for (int i = 0; i < queues.length; i++) {
            queues[i] = new TaskQueue(coreThreadPools);
        }

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
     * 在指定序号的线程池中执行任务
     * @param runnable
     * @param index
     */
    public void runIn(Runnable runnable, int index) {
        TaskQueue queue = getTaskQueue(index);
        queue.addTask(runnable);
    }

    /**
     * 在几个字段关联的线程池中，执行任务
     * @param runnable
     * @param bindValues
     */
    public void runBy(Runnable runnable, Object... bindValues) {
        TaskQueue queue = getTaskQueue(Objects.hash(bindValues));
        queue.addTask(runnable);
    }

    /**
     * 执行异步任务
     * @param runnable
     */
    public Future<?> runAsync(Runnable runnable) {
        return asyncThreadPool.submit(runnable);
    }

    /**
     * 获取任务队列
     * @param index
     * @return
     */
    private TaskQueue getTaskQueue(int index) {
        return queues[index & mark];
    }

}
