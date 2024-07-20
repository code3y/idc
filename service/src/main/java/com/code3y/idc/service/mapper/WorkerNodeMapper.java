package com.code3y.idc.service.mapper;

import com.code3y.idc.service.model.entity.WorkerNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author code3y
 * @description 针对表【worker_node(工作节点表)】的数据库操作Mapper
 * @createDate 2024-07-20 10:37:56
 * @Entity com.code3y.idc.service.model.entity.WorkerNode
 */
@Mapper
public interface WorkerNodeMapper extends BaseMapper<WorkerNode> {

    void saveWorkerNode(WorkerNode node);
}




