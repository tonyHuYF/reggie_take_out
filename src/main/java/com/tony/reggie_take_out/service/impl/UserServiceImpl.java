package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tony.reggie_take_out.common.CustomException;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.User;
import com.tony.reggie_take_out.mapper.UserMapper;
import com.tony.reggie_take_out.service.UserService;
import com.tony.reggie_take_out.untils.SMSUtils;
import com.tony.reggie_take_out.untils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 发送验证码
     */
    @Override
    public Result<String> sendMsg(User user, HttpSession session) {
        String phone = user.getPhone();

        try {
            //手机号不为空才进行
            if (ObjectUtil.isNotEmpty(phone)) {
                //生成随机6位验证码
                String code = ValidateCodeUtils.generateValidateCode(6).toString();
                log.info("生成验证码，code:{}", code);

                //发送短信
                String[] param = {code, "5"};
                SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE_2, phone, param);

                //保存验证码到session
                session.setAttribute(phone, code);
            }
        } catch (TencentCloudSDKException ex) {
            ex.printStackTrace();
            throw new CustomException("发送验证码失败");
        }

        return Result.success("发送验证码成功");
    }

    @Override
    public Result<User> login(Map map, HttpSession session) {
        log.info("map信息:{}", map);
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        String sessionCode = (String) session.getAttribute(phone);

        if (StrUtil.equals(code, sessionCode)) {
            //验证成功
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));

            if (user == null) {
                //为新用户创建user
                user = new User();
                user.setName(UUID.randomUUID().toString());
                user.setPhone(phone);
                user.setStatus(1);
                userMapper.insert(user);
            }
            session.setAttribute("user", user.getId());
            return Result.success(user);
        }

        return Result.error("登录失败");
    }
}
