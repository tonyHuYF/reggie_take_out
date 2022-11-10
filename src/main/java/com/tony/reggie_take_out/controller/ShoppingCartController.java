package com.tony.reggie_take_out.controller;

import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.ShoppingCart;
import com.tony.reggie_take_out.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Resource
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.add(shoppingCart);
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        return shoppingCartService.show();
    }

    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart) {
        return shoppingCartService.sub(shoppingCart);
    }

    @DeleteMapping("/clean")
    public Result<String> clean (){
        return shoppingCartService.clean();
    }

}
