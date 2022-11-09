package com.tony.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.CustomException;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Category;
import com.tony.reggie_take_out.entity.Dish;
import com.tony.reggie_take_out.entity.Setmeal;
import com.tony.reggie_take_out.mapper.CategoryMapper;
import com.tony.reggie_take_out.mapper.DishMapper;
import com.tony.reggie_take_out.mapper.SetmealMapper;
import com.tony.reggie_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类管理
 */

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private DishMapper dishMapper;
    @Resource
    private SetmealMapper setmealMapper;

    @Override
    public Result<String> insert(Category category) {
        log.info("新增分类,分类信息：{}", category.toString());
        categoryMapper.insert(category);
        return Result.success("新增分类成功");
    }

    @Override
    public Result<Page> page(int page, int pageSize) {
        log.info("page:{},pageSize:{}", page, pageSize);
        Page<Category> categoryPage = categoryMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        return Result.success(categoryPage);
    }

    @Override
    public Result<String> delete(Long id) {
        log.info("删除分类,分类信息id：{}", id);

        //是否关联了菜品
        Long count = dishMapper.selectCount(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id));
        if (count > 0) {
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        //是否关联了套餐
        Long count2 = setmealMapper.selectCount(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, id));
        if (count2 > 0) {
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        categoryMapper.deleteById(id);
        return Result.success("分类信息删除成功");
    }

    @Override
    public Result<String> update(Category category) {
        log.info("修改分类,分类信息：{}", category);
        categoryMapper.updateById(category);
        return Result.success("分类信息修改成功");
    }

    @Override
    public Result<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categories = categoryMapper.selectList(wrapper);
        return Result.success(categories);
    }
}
