package com.tony.reggie_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.DishDto;
import com.tony.reggie_take_out.entity.Dish;
import com.tony.reggie_take_out.service.DishService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/{id}")
    public Result<DishDto> getById(@PathVariable Long id){
        return dishService.getById(id);
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dto){
        return dishService.update(dto);
    }

    @GetMapping("/list")
    public Result<List<Dish>> list(Dish dish){
        return dishService.list(dish);
    }

}
