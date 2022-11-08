package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Employee;
import com.tony.reggie_take_out.mapper.EmployeeMapper;
import com.tony.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@Transactional
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
        request.getSession().setAttribute("employee", emp.getId());

        return Result.success(emp);
    }

    @Override
    public Result<String> logout(HttpServletRequest request) {
        //退出清除session
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    @Override
    public Result<String> insert(Employee employee) {

        log.info("新增员工,员工信息：{}", employee.toString());
        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employeeMapper.insert(employee);
        return Result.success("新增员工成功");
    }

    @Override
    public Result<Page> page(int page, int pageSize, String name) {
        log.info("page:{},pageSize:{},name:{}", page, pageSize, name);

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotEmpty(name), Employee::getName, name);
        wrapper.orderByDesc(Employee::getUpdateTime);

        Page<Employee> employeePage = employeeMapper.selectPage(new Page<>(page, pageSize), wrapper);

        return Result.success(employeePage);
    }

    @Override
    public Result<String> update(Employee employee) {
        log.info("修改员工,员工信息：{}", employee.toString());
        employeeMapper.updateById(employee);
        return Result.success("员工信息修改成功");
    }

    @Override
    public Result<Employee> getById(Long id) {
        log.info("根据id查询员工信息，员工id:{}", id);
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            return Result.error("没有查询到对应的员工信息");
        }
        return Result.success(employeeMapper.selectById(id));
    }
}
