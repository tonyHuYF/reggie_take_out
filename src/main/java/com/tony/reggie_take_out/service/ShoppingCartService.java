package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    public Result<ShoppingCart> add(ShoppingCart shoppingCart);

    public Result<List<ShoppingCart>> show();

    public Result<String> sub(ShoppingCart shoppingCart);

    public Result<String> clean();
}
