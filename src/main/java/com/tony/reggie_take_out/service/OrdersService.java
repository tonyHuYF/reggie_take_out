package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.OrdersDto;
import com.tony.reggie_take_out.entity.Orders;

public interface OrdersService extends IService<Orders> {
    public Result<String> submit(Orders orders);

    public Result<Page<OrdersDto>> userPage(int page, int pageSize);
}
