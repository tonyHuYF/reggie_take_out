package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {
    public Result<Employee> login(HttpServletRequest request, Employee employee);

    public Result<String> logout(HttpServletRequest request);
}
