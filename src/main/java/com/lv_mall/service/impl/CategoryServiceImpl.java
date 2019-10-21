package com.lv_mall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.dao.CategoryMapper;
import com.lv_mall.pojo.Category;
import com.lv_mall.service.ICategoryService;
import com.sun.tools.classfile.Opcode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service("ICategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    public ServiceResponse addCategory(String categoryName, Integer parentId){
        if(parentId==null|| StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount>0){
            return ServiceResponse.createBySuccessMessage("添加品类成功");
        }
        return ServiceResponse.createByErrorMessage("添加品类失败");
    }


    public ServiceResponse updateCategoryName(Integer categoryId, String categoryName){
        if(categoryId==null||StringUtils.isBlank(categoryName)){
            return ServiceResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServiceResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServiceResponse.createByErrorMessage("更新品类名字失败");

    }

    public ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (categoryList.isEmpty()){
            logger.info("未找到当前分类的自分类");
        }
        return ServiceResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id和孩子节点的id
     * @param categoryId
     * @return
     */
    public ServiceResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category categoryItem: categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServiceResponse.createBySuccess(categoryIdList);

    }
    // 递归算法，算出自节点
    private Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category!=null){
            categorySet.add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildrenCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }

}
