package com.code3y.idc.service.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: code3y
 * @Description: TODO
 */
public class NetUtil {
    public static String getLocalAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("fail to get local ip !");
        }
    }
}
