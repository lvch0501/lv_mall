package com.lv_mall.service;


import com.github.pagehelper.PageInfo;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.pojo.Product;
import com.lv_mall.vo.ProductDetailVo;

public interface IProductService {
    ServiceResponse saveOrUpdateProduct(Product product);
    ServiceResponse<String> setSaleStatus(Integer productId, Integer status);
    ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize);
    ServiceResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);
}
