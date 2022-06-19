package com.collect.util.strategy;

import com.collect.util.MD5Util;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;

@Qualifier
@Component
public class MD5Strategy implements PathEncryptStrategy {
    @Override
    public String getPath(Object... path) {
        StringBuilder s = new StringBuilder();
        for (Object o : path) {
            s.append(o);
        }
        return MD5Util.md5(s.toString()) + File.separator;
    }
}
