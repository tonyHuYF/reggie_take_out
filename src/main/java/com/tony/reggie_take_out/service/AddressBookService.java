package com.tony.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    public Result<AddressBook> insert(AddressBook addressBook);

    public Result<AddressBook> setDefault(AddressBook addressBook);

    public Result<List<AddressBook>> list(AddressBook addressBook);
}
