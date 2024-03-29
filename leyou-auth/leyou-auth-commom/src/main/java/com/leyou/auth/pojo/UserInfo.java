package com.leyou.auth.pojo;

import lombok.Data;

/**
 * 载荷对象
 * @author ycdong
 */
@Data
public class UserInfo {

    private Long id;

    private String username;

    public UserInfo() {
    }

    public UserInfo(Long id, String username) {
        this.id = id;
        this.username = username;
    }

}