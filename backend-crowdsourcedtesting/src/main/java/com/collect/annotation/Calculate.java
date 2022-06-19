package com.collect.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Calculate {
    double mul() default 1;

    double add() default 0;

    double pow() default 1;

    String desc() default "无说明";
}
