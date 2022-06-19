package com.collect.service;

import com.collect.dto.UserDTO;
import com.collect.po.enums.UserIdentity;
import com.collect.vo.ResponseVO;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 小鱼丁
 * @since 2022-02-20
 */
public interface UserService {
    ResponseVO register(UserDTO userDTO);

    ResponseVO login(UserDTO userDTO);

    boolean codeJudge(String code);

    UserIdentity getIdentityByUserId(int userId);
}
