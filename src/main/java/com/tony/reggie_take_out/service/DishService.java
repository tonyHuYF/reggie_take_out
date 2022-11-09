package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    public Result<String> insert(DishDto dishDto);

    public Result<Page<DishDto>> page(int page, int pageSize, String name);

    public Result<DishDto> getById (Long id);

    public Result<String> update(DishDto dto);

    public Result<List<Dish>> list(Dish dish);
}
