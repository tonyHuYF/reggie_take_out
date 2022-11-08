package com.tony.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Category;
import com.tony.reggie_take_out.mapper.CategoryMapper;
import com.tony.reggie_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 分类管理
 */

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

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
        categoryMapper.deleteById(id);
        return Result.success("分类信息删除成功");
    }
}
