package com.lv_mall.service;

import com.github.pagehelper.PageInfo;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.pojo.Shipping;

public interface IShippingService {
    ServiceResponse add(Integer userId, Shipping shipping);

    ServiceResponse<String> del(Integer userId, Integer shippingId);

    ServiceResponse update(Integer userId, Shipping shipping);

    ServiceResponse<Shipping> select(Integer userId, Integer shippingId);

    ServiceResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}

