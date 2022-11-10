package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.BaseContext;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.ShoppingCart;
import com.tony.reggie_take_out.mapper.ShoppingCartMapper;
import com.tony.reggie_take_out.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public Result<ShoppingCart> add(ShoppingCart shoppingCart) {

        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();

        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        if (ObjectUtil.isNotEmpty(dishId)) {
            //菜品
            wrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        } else {
            //套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }


        ShoppingCart cartDish = shoppingCartMapper.selectOne(wrapper);

        //查询当前菜品、套餐是否已经在购物车
        if (cartDish != null) {

            //已存在
            cartDish.setNumber(cartDish.getNumber() + 1);
            shoppingCartMapper.updateById(cartDish);
        } else {
            //不存在，即当前是套装
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
            cartDish = shoppingCart;
        }


        return Result.success(cartDish);
    }

    @Override
    public Result<List<ShoppingCart>> show() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        wrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(wrapper);
        return Result.success(shoppingCarts);
    }

    @Override
    public Result<String> sub(ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        if (ObjectUtil.isNotEmpty(dishId)) {
            //当前选中是菜品
            wrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //当前选中是套餐
            wrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }


        ShoppingCart cart = shoppingCartMapper.selectOne(wrapper);

        Integer number = cart.getNumber();

        if (number > 1) {
            cart.setNumber(cart.getNumber() - 1);
            shoppingCartMapper.updateById(cart);
        } else {
            shoppingCartMapper.deleteById(cart.getId());
        }
        return Result.success("数据减少成功");
    }

    @Override
    public Result<String> clean() {
        shoppingCartMapper.delete(new LambdaQueryWrapper<ShoppingCart>().eq(ShoppingCart::getUserId, BaseContext.getCurrentId()));
        return Result.success("购物车清空成功");
    }
}
