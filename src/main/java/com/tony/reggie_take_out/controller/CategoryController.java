package com.tony.reggie_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Category;
import com.tony.reggie_take_out.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 分类管理
 */

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private CategoryService categoryService;

    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        return categoryService.insert(category);
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize) {
        return categoryService.page(page, pageSize);
    }

    @DeleteMapping
    public Result<String> delete(Long id) {
        return categoryService.delete(id);
    }

}