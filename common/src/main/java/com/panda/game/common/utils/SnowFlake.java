package com.panda.game.common.utils;

import com.panda.game.common.log.Logger;
import com.panda.game.common.log.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 雪花算法生成id：每秒最大支持3.2万个<br/>
 * serverId(16) + timestamp(32) + sequence(15)<br/>
 */
public class SnowFlake {

    private static final Logger log = LoggerFactory.getLogger(SnowFlake.class);

    private static final int ServerId_Bit_Num = 16;
    private static final int Timestamp_Bit_Num = 32;
    private static final int Sequence_Bit_Num = 63 - ServerId_Bit_Num - Timestamp_Bit_Num;

    private static final int ServerId_Bit_Shift = Timestamp_Bit_Num + Sequence_Bit_Num;
    private static final int Timestamp_Bit_Shift = Sequence_Bit_Num;
    private static final int Sequence_Bit_Shift = 0;

    private static final long Sequence_Mark_Flag = (1L << Sequence_Bit_Num) - 1;
    private static final long Timestamp_Mark_Flag = (1L << Timestamp_Bit_Num) - 1;

    /** 设置一个时间起始点 */
    private static long Epoch = 0;
    private static long Default_Epoch_Date = 1671073633L; // 2022-12-15 10:38:50
    private static long Time_Offset = 5;

    private static long serverId;
    private static long sequence;
    private static long lastTime = System.currentTimeMillis() / 1000;

    /**
     * 初始化算法的字段
     * @param serverId
     * @param epochDate 初始化时间起点（null表示默认起始日期）,后期修改会导致id重复,如果要修改，时间只能往前改，不能往后改
     */
    public static void init(int serverId, Date epochDate) {
        if (serverId <= 0 || serverId >= (1 << ServerId_Bit_Num)) {
            throw new IllegalArgumentException("serverId is out of range, 0 ~ " + (1 >> ServerId_Bit_Num));
        }
        if (epochDate != null) {
            SnowFlake.Epoch = epochDate.getTime() / 1000;
        } else {
            SnowFlake.Epoch = Default_Epoch_Date;
        }

        SnowFlake.serverId = serverId;
    }

//    public static synchronized long getNextId2() {
//        long currentTime = System.currentTimeMillis();
//        long timestamp = currentTime / 1000;
//        if (timestamp < lastTime) {
//            // 防止时钟回拨
//            if(lastTime - timestamp < 2) {
//                timestamp = lastTime;
//            } else {
//                throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for {} s", lastTime - timestamp));
//            }
//        }
//        if (timestamp == lastTime) {
//            long newSequence = (sequence + 1) & Sequence_Mark_Flag;
//            if (newSequence == 0) {
//                timestamp = tilNextSecond(lastTime);
//            }
//            sequence = newSequence;
//        } else {
//            sequence = 0;
//        }
//        lastTime = timestamp;
//
//        return (serverId << ServerId_Bit_Shift)
//                | (sequence << Sequence_Bit_Shift)
//                | (((timestamp - Epoch) & Timestamp_Mark_Flag) << Timestamp_Bit_Shift);
//    }

    public static synchronized long getNextId() {
        long newSequence = (sequence + 1) & Sequence_Mark_Flag;
        if (newSequence == 0) {
            lastTime ++;

            long currentTime = System.currentTimeMillis() / 1000;
            if (lastTime - currentTime > Time_Offset) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    log.error("生成ID时，sleep中断", e);
                }
            }
        }
        sequence = newSequence;

        return (serverId << ServerId_Bit_Shift)
                | (sequence << Sequence_Bit_Shift)
                | (((lastTime - Epoch) & Timestamp_Mark_Flag) << Timestamp_Bit_Shift);
    }

//    private static long tilNextSecond(long lastSecond) {
//        long timestampNow = System.currentTimeMillis() / 1000;
//        while (timestampNow == lastSecond) {
//            timestampNow = System.currentTimeMillis() / 1000;
//        }
//        if (timestampNow < lastSecond) {
//            throw new RuntimeException("Clock moved backwards");
//        }
//
//        return timestampNow;
//    }

    public static void main(String[] args) throws InterruptedException {
        SnowFlake.init(1, null);
        long nextId = SnowFlake.getNextId();
        long nextId2 = SnowFlake.getNextId();

        String regex = String.format("(?<=\\d)((?=\\d{%d}$)|(?=\\d{%d}$)|(?=\\d{%d}$))", Timestamp_Bit_Num, ServerId_Bit_Shift, 63);
        System.out.println(regex);

        String paddingStr = leftPadding(Long.toBinaryString(nextId), 64, '0');
        System.out.println(paddingStr.replaceAll(regex, " | "));
        String paddingStr2 = leftPadding(Long.toBinaryString(nextId2), 64, '0');
        System.out.println(paddingStr2.replaceAll(regex, " | "));


        ConcurrentHashMap<Long, Integer> set = new ConcurrentHashMap<>(8192);
        AtomicInteger counter = new AtomicInteger(0);
        long start = System.currentTimeMillis();
        Thread.sleep(10000);
        int N = 100;
        Thread[] threadGroup = new Thread[N];
        for (int i = 0; i < threadGroup.length; i++) {
            int j = i;
            threadGroup[i] = new Thread(() -> {
                while (System.currentTimeMillis() - start < 60000) {
                    long lastId = SnowFlake.getNextId();
                    set.compute(lastId, (k, v) -> v == null ? 1 : v + 1);
                    counter.incrementAndGet();
                }
            });
        }

        System.out.println(new Date());
        for (int i = 0; i < threadGroup.length; i++) {
            threadGroup[i].start();
        }
        for (int i = 0; i < threadGroup.length; i++) {
            threadGroup[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.printf("generated %6d, cost time: %4d ms%n", counter.get(), (end - start));
        System.out.println(new Date(lastTime * 1000));

        int count2 = 0;
        for (Map.Entry<Long, Integer> entry : set.entrySet()) {
            if (entry.getValue() > 1) {
                String msg = leftPadding(Long.toBinaryString(entry.getValue()), 64, '0').replaceAll(regex, " | ");
                System.out.println(msg);

                count2 ++;
            }
        }
        System.out.println(count2);
    }

    private static String leftPadding(String content, int len, char padding) {
        if (content.length() < len) {
            StringBuilder sb = new StringBuilder(len);
            for (int i = content.length(); i < len; i++) {
                sb.append(padding);
            }
            sb.append(content);
            return sb.toString();
        }

        return content;
    }

}
