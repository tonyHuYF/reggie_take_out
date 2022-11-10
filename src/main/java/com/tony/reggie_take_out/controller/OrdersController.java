package com.tony.reggie_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.OrdersDto;
import com.tony.reggie_take_out.entity.Orders;
import com.tony.reggie_take_out.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
public class OrdersController {
    @Resource
    private OrdersService ordersService;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        return ordersService.submit(orders);
    }

    @GetMapping("/userPage")
    public Result<Page<OrdersDto>> userPage(int page, int pageSize) {
        return ordersService.userPage(page, pageSize);
    }
}
