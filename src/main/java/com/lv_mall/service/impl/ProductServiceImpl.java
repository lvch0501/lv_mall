package com.lv_mall.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.lv_mall.common.ResponseCode;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.dao.CategoryMapper;
import com.lv_mall.dao.ProductMapper;
import com.lv_mall.dao.UserMapper;
import com.lv_mall.pojo.Category;
import com.lv_mall.pojo.Product;
import com.lv_mall.service.IProductService;
import com.lv_mall.util.DateTimeUtil;
import com.lv_mall.util.PropertiesUtil;
import com.lv_mall.vo.ProductDetailVo;
import com.lv_mall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;
    public ServiceResponse saveOrUpdateProduct(Product product){
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId()!=null){
                int rowCount = productMapper.updateByPrimaryKey(product);
                if(rowCount>0){
                    return ServiceResponse.createBySuccess("更新产品成功");
                }else {
                    return ServiceResponse.createByErrorMessage("更新产品失败");
                }

            }else {
                int rowCount = productMapper.insert(product);
                if(rowCount>0){
                    return ServiceResponse.createBySuccess("新增产品成功");
                }else {
                    return ServiceResponse.createByErrorMessage("新增产品失败");
                }
            }
        }else {
            return ServiceResponse.createByErrorMessage("新增或更新产品信息不正确");
        }
    }


    public ServiceResponse<String> setSaleStatus(Integer productId, Integer status){
        if(productId==null||status==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return ServiceResponse.createBySuccessMessage("修改产品销售状态成功");
        }else {
            return ServiceResponse.createByErrorMessage("修改产品销售状态失败");
        }
    }


    public ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServiceResponse.createByErrorMessage("产品已下架或删除");
        }
        //VO对象 --value object
        //pojo -> bo(business object) -> vo(view project)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServiceResponse.createBySuccess(productDetailVo);
    }
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        //imageHost
        //parentCategoryId
        //CreateTime
        //updateTime
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","ftp://49.234.85.250/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            //默认根节点
            productDetailVo.setParentCategoryId(0);
        }else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;

    }


    public ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize){
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","ftp://49.234.85.250/"));
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServiceResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

}
