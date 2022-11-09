package com.tony.reggie_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tony.reggie_take_out.dto.SetmealDto;
import com.tony.reggie_take_out.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    Page<SetmealDto> page(Page page, @Param("name") String name);
}
