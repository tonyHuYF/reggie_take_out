package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.dto.SetmealDto;
import com.tony.reggie_take_out.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public Result<String> insert(SetmealDto dto);

    public Result<Page<SetmealDto>> page(int page, int pageSize, String name);

    public Result<String> delete(List<Long> ids);

    public Result<List<SetmealDto>> list(Setmeal setmeal);

    public Result<List<DishDto>> getBySetmealId(Long id);
}
