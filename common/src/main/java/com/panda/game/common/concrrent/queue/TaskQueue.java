package com.panda.game.common.concrrent.queue;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class TaskQueue {

    protected static Logger log = LoggerFactory.getLogger(TaskQueue.class);
    // 线程池
    private ThreadPoolExecutor executor;
    // 任务列表
    private BlockingQueue<Runnable> queue;

    public TaskQueue(ThreadPoolExecutor executor) {
        this.executor = executor;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void addTask(Runnable task) {
        int size = 0;
        synchronized(this) {
            this.queue.offer(task);
            size = this.queue.size();
        }

        if (size == 1) {
            Runnable currentTask = queue.peek();
            if (currentTask != null) {
                executor.execute(() -> {
                    executeTask(currentTask);
                });
            }
        } else if (size > 200) {
            log.info("TaskQueue size:{}", this.queue.size());
        }
    }

    private void executeTask(Runnable task) {
        try {
            task.run();
        } catch (Throwable e) {
            log.error("任务执行异常", e);
        } finally {
            if (!queue.remove(task)) {
                log.error("删除任务异常");
            }

            Runnable nextTask = queue.peek();
            if (nextTask != null) {
                executor.execute(() -> {
                    executeTask(nextTask);
                });
            }
        }
    }

}
