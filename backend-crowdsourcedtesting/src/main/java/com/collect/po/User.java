package com.collect.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.collect.dto.UserDTO;
import com.collect.po.enums.UserIdentity;
import com.collect.vo.UserVO;
import lombok.*;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 作者
 * @since 2022-02-20
 */
@Getter
@Setter
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements PO {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("email")
    private String email;

    @TableField("passwd")
    private String passwd;

    @TableField("salt")
    private String salt;

    @TableField("identity")
    private UserIdentity userIdentity;

    @TableField("name")
    private String name;

    @TableField("lastLogin")
    private Long lastLogin;

    @TableField("ip")
    private String ip;

    public User(String email, String passwd, UserIdentity userIdentity) {
        this.email = email;
        this.passwd = passwd;
        this.userIdentity = userIdentity;
    }

    public User(String email, String passwd, UserIdentity userIdentity, String name) {
        this.name = name;
        this.email = email;
        this.passwd = passwd;
        this.userIdentity = userIdentity;
    }

    public UserDTO toDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setUserIdentity(userIdentity);
        userDTO.setName(name);
        userDTO.setLastLogin(lastLogin);
        userDTO.setId(id);
        userDTO.setSalt(salt);
        userDTO.setPasswd(passwd);
        return userDTO;
    }
}
