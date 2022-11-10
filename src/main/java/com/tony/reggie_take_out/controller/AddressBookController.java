package com.tony.reggie_take_out.controller;

import com.tony.reggie_take_out.common.Result;
import com.tony.reggie_take_out.entity.AddressBook;
import com.tony.reggie_take_out.service.AddressBookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Resource
    private AddressBookService addressBookService;

    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook) {
        return addressBookService.insert(addressBook);
    }

    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        return addressBookService.setDefault(addressBook);
    }

    @GetMapping("/list")
    public Result<List<AddressBook>>list(AddressBook addressBook){
        return addressBookService.list(addressBook);

    }
}
