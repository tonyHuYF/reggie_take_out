package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.RedisConstant;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.entity.Category;
import com.tony.reggie_take_out.entity.Dish;
import com.tony.reggie_take_out.entity.DishFlavor;
import com.tony.reggie_take_out.mapper.CategoryMapper;
import com.tony.reggie_take_out.mapper.DishFlavorMapper;
import com.tony.reggie_take_out.mapper.DishMapper;
import com.tony.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishMapper dishMapper;
    @Resource
    private DishFlavorMapper dishFlavorMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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

        //删除缓存 (精准到某一菜品分类)
//        String key = RedisConstant.DISH_CATEGORY+dto.getCategoryId()+":1";
//        stringRedisTemplate.delete(key);

        //删除缓存，所有菜品分类
        stringRedisTemplate.delete(stringRedisTemplate.keys(RedisConstant.DISH_CATEGORY + "*"));


        return Result.success("修改菜品成功");
    }

    @Override
    public Result<List<DishDto>> list(Dish dish) {

        List<DishDto> dishDtoList = null;

        //动态拼接key
        String key = RedisConstant.DISH_CATEGORY + dish.getCategoryId() + ":" + dish.getStatus();

        //从redis拿数据
        dishDtoList = JSON.parseObject(stringRedisTemplate.opsForValue().get(key), List.class);

        if (dishDtoList != null) {
            return Result.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ObjectUtil.isNotEmpty(dish.getCategoryId()), Dish::getCategoryId, dish.getCategoryId());
        //只查询启售的
        wrapper.eq(Dish::getStatus, 1);
        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishes = dishMapper.selectList(wrapper);

        dishDtoList = dishes.stream().map((item) -> {
            DishDto dto = new DishDto();
            BeanUtil.copyProperties(item, dto);

            Category category = categoryMapper.selectById(item.getCategoryId());

            if (category != null) {
                dto.setCategoryName(category.getName());
            }

            //当前菜品的口味
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(
                    new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dto.getId()));
            dto.setFlavors(dishFlavors);

            return dto;
        }).collect(Collectors.toList());

        //插入数据到redis

        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(dishDtoList), 60, TimeUnit.MINUTES);


        return Result.success(dishDtoList);
    }
}
