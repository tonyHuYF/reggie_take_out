package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.entity.Dish;
import com.tony.reggie_take_out.entity.DishFlavor;
import com.tony.reggie_take_out.mapper.DishFlavorMapper;
import com.tony.reggie_take_out.mapper.DishMapper;
import com.tony.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Override
    public Result<String> insert(DishDto dishDto) {
        log.info("新增菜品,菜品信息：{}", dishDto);
        dishMapper.insert(dishDto);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(p -> {
            p.setDishId(dishDto.getId());
            dishFlavorMapper.insert(p);
        });

        return Result.success("新增菜品成功");
    }

    @Override
    public Result<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<DishDto> result = dishMapper.page(new Page<DishDto>(page, pageSize), name);
        return Result.success(result);
    }

    @Override
    public Result<DishDto> getById(Long id) {
        DishDto dto = new DishDto();
        Dish dish = dishMapper.selectById(id);
        if (dish != null) {
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId()));
            BeanUtil.copyProperties(dish, dto);
            dto.setFlavors(dishFlavors);
            return Result.success(dto);
        }
        return Result.error("查询菜品失败");
    }

    @Override
    public Result<String> update(DishDto dto) {
        Dish dish = new Dish();
        BeanUtil.copyProperties(dto, dish);
        dishMapper.updateById(dish);

        //对于口味，先删除，后新增
        dishFlavorMapper.delete(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId()));

        List<DishFlavor> flavors = dto.getFlavors();
        flavors.forEach(p -> {
            p.setDishId(dish.getId());
            dishFlavorMapper.insert(p);
        });
        return Result.success("修改菜品成功");
    }
}
