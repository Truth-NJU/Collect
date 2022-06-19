package com.collect.util.strategy;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class BuilderStrategy implements PathEncryptStrategy {
    @Override
    public String getPath(Object... path) {
        StringBuilder s = new StringBuilder();
        for (Object o : path) {
            s.append(o).append(File.separator);
        }
        return s.toString();
    }
}
