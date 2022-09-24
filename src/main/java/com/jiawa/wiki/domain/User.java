package com.jiawa.wiki.domain;

import java.io.Serializable;

/**
 * user
 * @author 
 */
public class User implements Serializable {
    /**
     * ID
     */
    private Long id;

    /**
     * 登陆名
     */
    private String loginName;

    /**
     * 昵称
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}