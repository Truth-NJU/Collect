package com.collect.vo;

import com.collect.annotation.UserId;
import com.collect.dto.UserDTO;
import com.collect.po.enums.UserIdentity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class UserVO implements VO {

    @UserId
    Integer id;

    String email;

    String passwd;

    String code;

    UserIdentity userIdentity;

    String name;

    private Long lastLogin;

    private String ip;

    public UserVO(int id, String email, UserIdentity userIdentity) {
        this.id = id;
        this.email = email;
        this.userIdentity = userIdentity;
    }

    public UserVO(String email, String passwd, String code, UserIdentity userIdentity) {
        this.email = email;
        this.passwd = passwd;
        this.code = code;
        this.userIdentity = userIdentity;
    }

    public UserVO(String email, String passwd, String code, UserIdentity userIdentity,String name) {
        this.name=name;
        this.email = email;
        this.passwd = passwd;
        this.code = code;
        this.userIdentity = userIdentity;
    }


    public UserDTO toDTO() {
        return new UserDTO(email, passwd, userIdentity,name);
    }
}
