package com.code3y.idc.service.service;

import com.code3y.idc.service.model.entity.WorkerNode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author code3y
 * @description 针对表【worker_node(工作节点表)】的数据库操作Service
 * @createDate 2024-07-20 10:37:56
 */
public interface WorkerNodeService extends IService<WorkerNode> {
    long assignWorkerId();
}
