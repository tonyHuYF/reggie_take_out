package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.CustomException;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.dto.SetmealDto;
import com.tony.reggie_take_out.entity.*;
import com.tony.reggie_take_out.mapper.*;
import com.tony.reggie_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealDishMapper setmealDishMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;

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

    @Override
    public Result<List<SetmealDto>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtil.isNotEmpty(setmeal.getId()), Setmeal::getId, setmeal.getId());
        wrapper.eq(Setmeal::getStatus, 1);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealMapper.selectList(wrapper);

        List<SetmealDto> dtoList = setmeals.stream().map((item) -> {
            SetmealDto dto = new SetmealDto();
            BeanUtil.copyProperties(item, dto);

            Category category = categoryMapper.selectById(dto.getCategoryId());

            if (category != null) {
                dto.setCategoryName(category.getName());
            }

            //套餐对应的菜品
            List<SetmealDish> setmealDishes = setmealDishMapper.selectList(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, dto.getId()));
            dto.setSetmealDishes(setmealDishes);

            return dto;
        }).collect(Collectors.toList());

        return Result.success(dtoList);
    }

    @Override
    public Result<List<DishDto>> getBySetmealId(Long id) {
        List<SetmealDish> setmealDishes = setmealDishMapper.selectList(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));

        List<DishDto> dtoList = setmealDishes.stream().map((item) -> {
            DishDto dto = new DishDto();
            Dish dish = dishMapper.selectById(item.getDishId());

            if (dish != null) {
                BeanUtil.copyProperties(dish, dto);

                Category category = categoryMapper.selectById(dto.getCategoryId());

                if (category != null) {
                    dto.setCategoryName(category.getName());
                }

                //添加口味
                List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dto.getId()));
                dto.setFlavors(dishFlavors);
            }

            //份数
            dto.setCopies(item.getCopies());

            return dto;
        }).collect(Collectors.toList());

        return Result.success(dtoList);
    }
}
