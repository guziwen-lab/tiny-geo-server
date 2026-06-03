package com.supermap.common.util;

import java.time.Instant;

public class SnowflakeIdGenerator {
    // Epoch 起始时间戳 (可以自定义)
    private static final long EPOCH = 1609459200000L; // 2021-01-01 00:00:00

    // 位数分配
    private static final long DATA_CENTER_ID_BITS = 5L; // 数据中心 ID 所占位数
    private static final long MACHINE_ID_BITS = 5L;     // 机器 ID 所占位数
    private static final long SEQUENCE_BITS = 12L;      // 序列号所占位数

    // 最大值计算
    private static final long MAX_DATA_CENTER_ID = (1L << DATA_CENTER_ID_BITS) - 1;
    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // 位移
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATA_CENTER_ID_BITS;

    private final long dataCenterId;  // 数据中心 ID
    private final long machineId;     // 机器 ID
    private long sequence = 0L;       // 当前毫秒内序列号
    private long lastTimestamp = -1L; // 上次生成 ID 的时间戳

    public SnowflakeIdGenerator(long dataCenterId, long machineId) {
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER_ID) {
            throw new IllegalArgumentException(String.format("DataCenter ID must be between 0 and %d", MAX_DATA_CENTER_ID));
        }
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(String.format("Machine ID must be between 0 and %d", MAX_MACHINE_ID));
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        if (currentTimestamp == lastTimestamp) {
            // 同一毫秒内，递增序列号
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 序列号用完，等待下一毫秒
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 生成 ID
        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    private long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    public static void main(String[] args) {
        // 示例：数据中心 ID 为 1，机器 ID 为 1
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);

        // 生成 ID
        for (int i = 0; i < 10; i++) {
            System.out.println(generator.nextId());
        }
    }
}