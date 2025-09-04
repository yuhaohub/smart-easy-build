package com.yuhao.smarteasybuild.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

/**
 * 生成缓存 key 工具类
 */
public class CacheKeyUtils {

    public static String generateCacheKey(Object obj) {
        if(obj == null){
            return DigestUtil.md5Hex("null");
        }
        String jsonStr = JSONUtil.toJsonStr(obj);
        return DigestUtil.md5Hex(jsonStr);
    }
}
