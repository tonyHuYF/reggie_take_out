package com.tony.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Employee;
import com.tony.reggie_take_out.mapper.EmployeeMapper;
import com.tony.reggie_take_out.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Resource
    private EmployeeMapper employeeMapper;

    @Override
    public Result<Employee> login(HttpServletRequest request, Employee employee) {
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeMapper.selectOne(wrapper);

        if (emp == null || !emp.getPassword().equals(password)) {
            return Result.error("登录失败");
        }

        if (emp.getStatus() == 0) {
            return Result.error("账户已禁用");
        }

        //存一份数据到session
        request.getSession().setAttribute("employee", employee.getId());

        return Result.success(emp);
    }

    @Override
    public Result<String> logout(HttpServletRequest request) {
        //退出清除session
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }
}
