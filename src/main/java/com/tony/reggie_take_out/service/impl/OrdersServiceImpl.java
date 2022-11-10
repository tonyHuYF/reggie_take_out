package com.tony.reggie_take_out.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.BaseContext;
import com.tony.reggie_take_out.common.CustomException;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.dto.OrdersDto;
import com.tony.reggie_take_out.entity.*;
import com.tony.reggie_take_out.mapper.*;
import com.tony.reggie_take_out.service.OrderDetailService;
import com.tony.reggie_take_out.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
    @Resource
    private AddressBookMapper addressBookMapper;
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private ShoppingCartServiceImpl shoppingCartService;

    @Override
    public Result<String> submit(Orders orders) {
        AddressBook addressBook = addressBookMapper.selectById(orders.getAddressBookId());
        Long userId = BaseContext.getCurrentId();

        if (addressBook == null) {
            throw new CustomException("下单地址不能为空");
        }


        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectList(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId));

        if (shoppingCarts == null) {
            throw new CustomException("下单购物车不能为空");
        }

        AtomicInteger amount = new AtomicInteger(0);

        long orderId = IdWorker.getId();

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail detail = new OrderDetail();
            BeanUtil.copyProperties(item, detail);
            detail.setOrderId(orderId);

            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return detail;
        }).collect(Collectors.toList());


        User user = userMapper.selectById(userId);


        orders.setId(orderId);
        orders.setNumber(String.valueOf(orderId));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setConsignee(addressBook.getConsignee());

        //插入订单表
        ordersMapper.insert(orders);

        //插入订单明细表
        orderDetailService.saveBatch(orderDetails);

        //清空购物车
        shoppingCartService.clean();

        return Result.success("下单成功");
    }

    @Override
    public Result<Page<OrdersDto>> userPage(int page, int pageSize) {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        Page<Orders> ordersPage = ordersMapper.selectPage(new Page<>(page, pageSize), wrapper);


        Page<OrdersDto> dtoPage = new Page<>();

        BeanUtil.copyProperties(ordersPage, dtoPage);


        List<OrdersDto> ordersDtoList = ordersPage.getRecords().stream().map((item) -> {
            OrdersDto dto = new OrdersDto();
            BeanUtil.copyProperties(item, dto);

            List<OrderDetail> orderDetails = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>().eq(OrderDetail::getOrderId, item.getId()));

            dto.setOrderDetails(orderDetails);

            return dto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(ordersDtoList);

        return Result.success(dtoPage);
    }
}
