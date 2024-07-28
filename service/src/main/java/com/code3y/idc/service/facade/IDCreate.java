package com.code3y.idc.service.facade;

import com.code3y.idc.contract.facade.IIDCreate;
import com.code3y.idc.service.buffer.BufferPaddingExecutor;
import com.code3y.idc.service.model.po.BitsAllocator;
import com.code3y.idc.service.buffer.CircleBuffer;
import com.code3y.idc.service.service.WorkerNodeService;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: code3y
 * @Description: TODO
 */
@DubboService
public class IDCreate implements IIDCreate, InitializingBean {
    protected int timeBits = 28;
    protected int workerBits = 22;
    protected int seqBits = 13;

    private int boostPower = 3;
    private BitsAllocator bitsAllocator;
    private long workerId;
    private CircleBuffer circleBuffer;

    protected long epochSeconds = TimeUnit.MILLISECONDS.toSeconds(1463673600000L);

    private BufferPaddingExecutor bufferPaddingExecutor;

    @Value("${buffer.executor.schedule}")
    private Boolean schedule;

    @Resource
    private WorkerNodeService workerNodeService;

    @Override
    public long get() {
        return circleBuffer.get();
    }

    @Override
    public List<Long> get(int count) {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initBitsAllocator();
        initCircleBuffer();
        initBufferPaddingExecutor();
    }

    private void initBufferPaddingExecutor() {
        BufferPaddingExecutor executor = new BufferPaddingExecutor(circleBuffer, schedule, this::nextIdsForOneSecond);
        this.bufferPaddingExecutor = executor;
        circleBuffer.setBufferPaddingExecutor(executor);
        bufferPaddingExecutor.paddingBuffer();
        bufferPaddingExecutor.start();
    }

    private void initCircleBuffer() {
        int bufferSize = ((int) bitsAllocator.getMaxSequence() + 1) << boostPower;
        circleBuffer = new CircleBuffer(bufferSize, CircleBuffer.DEFAULT_PADDING_PERCENT);
    }

    private void initBitsAllocator() {
        bitsAllocator = new BitsAllocator(timeBits, workerBits, seqBits);
        long workerId = workerNodeService.assignWorkerId();
        if (workerId > bitsAllocator.getMaxWorkerId()) {
            throw new RuntimeException("Worker id " + workerId + " exceeds the max " + bitsAllocator.getMaxWorkerId());
        }
        this.workerId = workerId;
    }

    private List<Long> nextIdsForOneSecond(long currentSecond) {
        int listSize = (int) bitsAllocator.getMaxSequence() + 1;
        List<Long> uidList = new ArrayList<>(listSize);
        long firstSeqUid = bitsAllocator.allocate(currentSecond - epochSeconds, workerId, 0L);
        for (int offset = 0; offset < listSize; offset++) {
            uidList.add(firstSeqUid + offset);
        }

        return uidList;
    }
}
