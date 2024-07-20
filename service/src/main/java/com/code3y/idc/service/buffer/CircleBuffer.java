package com.code3y.idc.service.buffer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: code3y
 * @Description: TODO
 */
@Slf4j
public class CircleBuffer {
    private static final int START_POINT = -1;
    private static final long CAN_PUT_FLAG = 0L;
    private static final long CAN_TAKE_FLAG = 1L;
    public static final int DEFAULT_PADDING_PERCENT = 50;

    private final int paddingThreshold;

    private final int bufferSize;
    private final long indexMask;
    private final long[] slots;
    private final long[] flags;

    private final AtomicLong tail;

    private final AtomicLong cursor;

    public BufferPaddingExecutor getBufferPaddingExecutor() {
        return bufferPaddingExecutor;
    }

    public void setBufferPaddingExecutor(BufferPaddingExecutor bufferPaddingExecutor) {
        this.bufferPaddingExecutor = bufferPaddingExecutor;
    }

    private BufferPaddingExecutor bufferPaddingExecutor;

    public CircleBuffer(int bufferSize, int paddingFactor) {
        Assert.isTrue(bufferSize > 0, "bufferSize is not legal! this must be positive!");
        Assert.isTrue(paddingFactor > 0 && paddingFactor < 100, "paddingFactor must be > 0 and < 100 !");
        this.bufferSize = bufferSize;
        this.indexMask = bufferSize - 1;
        this.slots = new long[bufferSize];
        this.flags = new long[bufferSize];
        this.paddingThreshold = bufferSize * paddingFactor / 100;
        this.tail = new AtomicLong(START_POINT);
        this.cursor = new AtomicLong(START_POINT);
    }

    public synchronized boolean put(long uid) {
        long curTail = tail.get();
        long curCursor = cursor.get();
        long distance = curTail - (curCursor == START_POINT ? 0 : curCursor);
        if (distance == bufferSize - 1) {
            log.warn("CircleBuffer is full can't put into");
            return false;
        }
        int nextTail = calSlotIndex(curTail + 1);
        if (flags[nextTail] != CAN_PUT_FLAG) {
            log.error("CircleBuffer put uid={} into buffer={} error", uid, this);
            return false;
        }
        slots[nextTail] = uid;
        flags[nextTail] = CAN_TAKE_FLAG;
        tail.incrementAndGet();
        return true;
    }

    public long get() {
        long curCursor = cursor.get();
        long nextCursor = cursor.updateAndGet(old -> old == tail.get() ? old : old + 1);
        Assert.isTrue(nextCursor >= curCursor, "Cursor can't move back!");
        long curTail = tail.get();
        if (curTail - nextCursor < paddingThreshold) {
            bufferPaddingExecutor.asyncPaddingBuffer();
        }
        int index = calSlotIndex(nextCursor);
        Assert.isTrue(flags[index] == CAN_TAKE_FLAG, "Cursor can't take now!");
        flags[index] = CAN_PUT_FLAG;
        return slots[index];
    }

    private int calSlotIndex(long sequence) {
        return (int) (sequence & indexMask);
    }
}
