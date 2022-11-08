package com.tony.reggie_take_out.common;

/**
 * 基于ThreadLocal，封装用户id
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }


    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
