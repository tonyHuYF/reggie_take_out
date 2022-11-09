package com.tony.reggie_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.service.DishService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        return dishService.insert(dishDto);
    }

    @GetMapping("/page")
    public Result<Page<DishDto>> page(int page, int pageSize, String name) {
        return dishService.page(page, pageSize, name);
    }

}
