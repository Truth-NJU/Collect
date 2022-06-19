package com.collect.util.strategy;

public interface PathEncryptStrategy {
    /**
     * 将路径拼接为加密路径
     * @param path，多个路径
     * @return 加密后的路径
     */
    String getPath(Object... path);
}
