package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.CustomException;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.SetmealDto;
import com.tony.reggie_take_out.entity.Setmeal;
import com.tony.reggie_take_out.entity.SetmealDish;
import com.tony.reggie_take_out.mapper.SetmealDishMapper;
import com.tony.reggie_take_out.mapper.SetmealMapper;
import com.tony.reggie_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Override
    public Result<String> insert(SetmealDto dto) {
        log.info("新增套餐,套餐信息：{}", dto);
        setmealMapper.insert(dto);
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();

        setmealDishes.forEach(p -> {
            p.setSetmealId(dto.getId());
            setmealDishMapper.insert(p);
        });

        return Result.success("新增套餐成功");
    }

    @Override
    public Result<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<SetmealDto> setmealDtoPage = setmealMapper.page(new Page<SetmealDto>(page, pageSize), name);
        return Result.success(setmealDtoPage);
    }


    @Override
    public Result<String> delete(List<Long> ids) {
        log.info("删除套餐,套餐ids：{}", ids);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ObjectUtil.isNotEmpty(ids), Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, 1);

        Long count = setmealMapper.selectCount(wrapper);

        if (count > 0) {
            throw new CustomException("套餐不是停用状态，无法删除");
        }

        setmealMapper.delete(new LambdaQueryWrapper<Setmeal>().in(ObjectUtil.isNotEmpty(ids), Setmeal::getId, ids));
        //删除套餐关联的菜品信息
        setmealDishMapper.delete(new LambdaQueryWrapper<SetmealDish>().in(ObjectUtil.isNotEmpty(ids), SetmealDish::getSetmealId, ids));
        return Result.success("删除套餐成功");
    }
}
