package com.code3y.idc.service.buffer;

import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: code3y
 * @Description: TODO
 */
@Slf4j
public class BufferPaddingExecutor {
    private CircleBuffer circleBuffer;
    private final BufferUidProvider uidProvider;
    private final AtomicBoolean running;
    private final ExecutorService bufferPadExecutors;
    private final ScheduledExecutorService bufferPadSchedule;

    private final AtomicLong lastSecond;

    public BufferPaddingExecutor(CircleBuffer buffer, boolean schedule, BufferUidProvider uidProvider) {
        this.circleBuffer = buffer;
        this.running = new AtomicBoolean(false);
        this.uidProvider = uidProvider;
        int cores = Runtime.getRuntime().availableProcessors();
        this.bufferPadExecutors = Executors.newFixedThreadPool(cores * 2);
        if (schedule) {
            this.bufferPadSchedule = Executors.newSingleThreadScheduledExecutor();
        } else {
            this.bufferPadSchedule = null;
        }
        this.lastSecond = new AtomicLong(TimeUnit.MICROSECONDS.toSeconds(System.currentTimeMillis()));

    }

    public void start() {
        if (bufferPadSchedule != null) {
            bufferPadSchedule.scheduleWithFixedDelay(() -> paddingBuffer(), 300, 300, TimeUnit.SECONDS);
        }
    }

    public void paddingBuffer() {
        if (!running.compareAndSet(false, true)) {
            log.warn("bufferPadExecutors is running !");
            return;
        }
        boolean isFullRingBuffer = false;
        while (!isFullRingBuffer) {
            List<Long> uidList = uidProvider.provider(lastSecond.incrementAndGet());
            for (Long uid : uidList) {
                isFullRingBuffer = !circleBuffer.put(uid);
                if (isFullRingBuffer) {
                    break;
                }
            }
        }
        running.compareAndSet(true, false);
    }

    public void asyncPaddingBuffer() {
        this.bufferPadExecutors.submit(this::paddingBuffer);
    }
}
