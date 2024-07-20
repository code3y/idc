package com.code3y.idc.service.buffer;

import java.util.List;

/**
 * @Author: code3y
 * @Description: TODO
 */
@FunctionalInterface
public interface BufferUidProvider {
    List<Long> provider(long momentInSecond);
}
