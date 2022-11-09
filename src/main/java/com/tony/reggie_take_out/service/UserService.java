package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService extends IService<User> {
    public Result<String> sendMsg(User user, HttpSession session);

    public Result<User> login(Map map, HttpSession session);
}
