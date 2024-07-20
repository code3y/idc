package com.code3y.idc.service.model.po;

import lombok.Data;
import org.springframework.util.Assert;

/**
 * @Author: code3y
 * @Description: TODO
 */
@Data
public class BitsAllocator {
    public static final int TOTAL_BITS = 1 << 6;
    /**
     * signBits : 符号位
     * timestampBits:时间戳
     * workerIdBits:工作机器id
     * sequenceBits:序列号
     */
    private int signBits = 1;
    private final int timestampBits;
    private final int workerIdBits;
    private final int sequenceBits;

    private final long maxDeltaSeconds;
    private final long maxWorkerId;
    private final long maxSequence;
    private final int timestampShift;
    private final int workerIdShift;

    public BitsAllocator(int timestampBits, int workerIdBits, int sequenceBits) {
        int allocateTotalBits = signBits + timestampBits + workerIdBits + sequenceBits;
        Assert.isTrue(allocateTotalBits == TOTAL_BITS, "allocate not enough 64 bits");
        this.timestampBits = timestampBits;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;
        this.maxDeltaSeconds = ~(-1L << timestampBits);
        this.maxWorkerId = ~(-1L << workerIdBits);
        this.maxSequence = ~(-1L << sequenceBits);
        this.timestampShift = workerIdBits + sequenceBits;
        this.workerIdShift = sequenceBits;
    }

    public long allocate(long deltaSeconds, long workerId, long sequence) {
        return (deltaSeconds << timestampShift) | (workerId << workerIdShift) | sequence;
    }
}
