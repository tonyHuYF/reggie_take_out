package com.tony.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tony.reggie_take_out.common.BaseContext;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.AddressBook;
import com.tony.reggie_take_out.mapper.AddressBookMapper;
import com.tony.reggie_take_out.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Resource
    private AddressBookMapper addressBookMapper;

    @Override
    public Result<AddressBook> insert(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.insert(addressBook);
        return Result.success(addressBook);
    }

    @Override
    public Result<AddressBook> setDefault(AddressBook addressBook) {
        //先将当前用户的所有地址设为非默认
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);

        addressBookMapper.update(new AddressBook(), wrapper);

        //将传过来的地址设为默认地址

        addressBook.setIsDefault(1);
        addressBookMapper.updateById(addressBook);
        return Result.success(addressBook);
    }

    @Override
    public Result<List<AddressBook>> list(AddressBook addressBook) {
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookMapper.selectList(wrapper);
        return Result.success(list);
    }
}
