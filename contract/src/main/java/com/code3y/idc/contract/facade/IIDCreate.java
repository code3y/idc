package com.code3y.idc.contract.facade;

import java.util.List;

/**
 * @Author: code3y
 * @Description: TODO
 */
public interface IIDCreate {
    //获取一个id
    long get();
    //批量获取id
    List<Long> get(int count);
}
