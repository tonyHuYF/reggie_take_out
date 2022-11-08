package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {
    public Result<Employee> login(HttpServletRequest request, Employee employee);

    public Result<String> logout(HttpServletRequest request);

    public Result<String> save(HttpServletRequest request, Employee employee);

    Result<Page> page(int page, int pageSize, String name);

    Result<String> update(HttpServletRequest request, Employee employee);

    Result<Employee> getById(Long id);
}

