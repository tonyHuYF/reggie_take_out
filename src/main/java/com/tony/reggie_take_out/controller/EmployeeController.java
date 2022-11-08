package com.tony.reggie_take_out.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Employee;
import com.tony.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.login(request, employee);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        return employeeService.logout(request);
    }

    @PostMapping
    public Result<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.save(request, employee);
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        return employeeService.page(page, pageSize, name);
    }

    @PutMapping
    public Result<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        long id = Thread.currentThread().getId();
        log.info("本次线程的id:{}", id);
        return employeeService.update(request, employee);
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        return employeeService.getById(id);
    }
}
