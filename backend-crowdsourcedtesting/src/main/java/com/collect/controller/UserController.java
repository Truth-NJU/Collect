package com.collect.controller;

import com.collect.annotation.CheckLoginStatus;
import com.collect.dto.UserDTO;
import com.collect.po.enums.UserIdentity;
import com.collect.service.AttributeService;
import com.collect.service.RuleService;
import com.collect.service.UserService;
import com.collect.util.DataCheck;
import com.collect.vo.*;
import com.collect.vo.http.UserHttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 小鱼丁 touzhihou
 * @since 2022-02-20
 */
@RestController
@RequestMapping("/collect/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AttributeService attributeService;

    @Autowired
    RuleService ruleService;

    @CheckLoginStatus(insert = CheckLoginStatus.InsertMode.NONE)
    @GetMapping("/tryLogin")
    public ResponseVO tryLogin() {
        return ResponseVO.succeed();
    }

    @PostMapping("/register")
    public ResponseVO register(@RequestBody UserVO userVO) {
        if (DataCheck.EmailCheck(userVO.getEmail())
                || DataCheck.StringCheck(userVO.getPasswd(), 250)
                || DataCheck.EnumCheck(userVO.getUserIdentity())) {
            return ResponseVO.fail(UserHttpStatus.DATA_ERROR);
        }
        if (userVO.getUserIdentity() == UserIdentity.ADMINISTRATOR) {
            if (DataCheck.StringCheck(userVO.getCode(), 18)) {
                return ResponseVO.fail(UserHttpStatus.DATA_ERROR);
            }
            if (!userService.codeJudge(userVO.getCode())) {
                return ResponseVO.fail(UserHttpStatus.CODE_ERROR);
            }
        }
        UserDTO userDTO = userVO.toDTO();
        return userService.register(userDTO);
    }

    @PostMapping("/login")
    public ResponseVO login(@RequestBody UserVO userVO) {
        if (DataCheck.EmailCheck(userVO.getEmail())
                || DataCheck.StringCheck(userVO.getPasswd(), 250)) {
            return ResponseVO.fail(UserHttpStatus.DATA_ERROR);
        }
        UserDTO userDTO = userVO.toDTO();
        // 根据lastLogin是否存在来判断是否是第一次登陆，不需要调用IfIn方法
        return userService.login(userDTO);
    }

    @CheckLoginStatus(identity = UserIdentity.WORKER)
    @PostMapping("/init")
    public ResponseVO init(@RequestBody AttributeVO attributeVO) {
        return attributeService.init(attributeVO.toDTO());
    }

    @GetMapping("/getRules")
    public ResponseVO getRules() {
        return ruleService.getRules();
    }

    @PostMapping("/setRules")
    public ResponseVO setRules(@RequestBody RuleVOS ruleVOS) {
        return ruleService.setRules(ruleVOS.getRuleVOS());
    }
}

