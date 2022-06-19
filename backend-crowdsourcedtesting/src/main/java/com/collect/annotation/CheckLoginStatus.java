package com.collect.annotation;

import com.collect.po.enums.UserIdentity;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckLoginStatus {
    /**
     * identity: 检验用户身份
     * insert：决定用户userId注入方式
     * login：取得登陆状态(failed)
     * @return
     */

    UserIdentity identity() default UserIdentity.ALL;

    InsertMode insert() default InsertMode.ENTITY;

    /**
     * NONE: 不注入，
     * ENTITY: 注入controller层参数，
     * THREAD：注入ThreadLocal
     */
    enum InsertMode {
        NONE, ENTITY, THREAD
    }
}
