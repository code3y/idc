package com.code3y.idc.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code3y.idc.service.common.util.NetUtil;
import com.code3y.idc.service.model.entity.WorkerNode;
import com.code3y.idc.service.service.WorkerNodeService;
import com.code3y.idc.service.mapper.WorkerNodeMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author code3y
 * @description 针对表【worker_node(工作节点表)】的数据库操作Service实现
 * @createDate 2024-07-20 10:37:56
 */
@Service
public class WorkerNodeServiceImpl extends ServiceImpl<WorkerNodeMapper, WorkerNode> implements WorkerNodeService {
    @Value("${server.port}")
    private Integer port;
    @Resource
    private WorkerNodeMapper workerNodeMapper;

    @Override
    public long assignWorkerId() {
        WorkerNode node = buildWorkerNode();
        workerNodeMapper.saveWorkerNode(node);
        return node.getId();
    }

    private WorkerNode buildWorkerNode() {
        WorkerNode workerNode = new WorkerNode();
        workerNode.setHostName(NetUtil.getLocalAddress());
        workerNode.setPort(String.valueOf(port));
        workerNode.setCreateTime(new Date());
        workerNode.setUpdateTime(new Date());
        return workerNode;
    }
}




