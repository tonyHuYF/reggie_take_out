package com.tony.reggie_take_out.controller;


import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Employee;
import com.tony.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        return employeeService.login(request, employee);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        return employeeService.logout(request);
    }
}
