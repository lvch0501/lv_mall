package com.lv_mall.controller.portal;


import com.lv_mall.common.ServiceResponse;
import com.lv_mall.service.IProductService;
import com.lv_mall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    public ServiceResponse<ProductDetailVo> detail(){
        return null;
    }
}
