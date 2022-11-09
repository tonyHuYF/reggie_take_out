package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.entity.Dish;

public interface DishService extends IService<Dish> {
    public Result<String> insert(DishDto dishDto);

    public Result<Page<DishDto>> page(int page, int pageSize, String name);
}
