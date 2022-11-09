package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    public Result<String> insert(Category category);

    public Result<Page> page(int page, int pageSize);

    public Result<String> delete(Long id);

    public Result<String> update(Category category);

    public Result<List<Category>> list(Category category);
}
