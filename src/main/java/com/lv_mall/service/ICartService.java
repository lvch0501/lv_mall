package com.lv_mall.service;

import com.lv_mall.common.ServiceResponse;
import com.lv_mall.vo.CartVo;

public interface ICartService {
    ServiceResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServiceResponse<CartVo> update(Integer userId, Integer productId, Integer count);
    ServiceResponse<CartVo> deleteProduct(Integer userId, String productIds);
    ServiceResponse<CartVo> list(Integer userId);
    ServiceResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);
    ServiceResponse<Integer> getCartProductCount(Integer userId);
}
