package com.tony.reggie_take_out.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.SetmealDto;
import com.tony.reggie_take_out.service.SetmealService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetmealService setmealService;

    @PostMapping
    public Result<String> save(@RequestBody SetmealDto dto) {
        return setmealService.insert(dto);
    }

    @GetMapping("/page")
    public Result<Page<SetmealDto>> page(int page, int pageSize, String name) {
        return setmealService.page(page, pageSize, name);
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam(value = "ids") List<Long> ids) {
        return setmealService.delete(ids);
    }
}
