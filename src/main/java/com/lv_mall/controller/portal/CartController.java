package com.lv_mall.controller.portal;


import com.lv_mall.common.Const;
import com.lv_mall.common.ResponseCode;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.pojo.User;
import com.lv_mall.service.ICartService;

import com.lv_mall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;


    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(), productId, count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(), productId, count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServiceResponse<CartVo> delete_product(HttpSession session, String productIds){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    //全选
    //全反选




    @RequestMapping("select_all.do")
    @ResponseBody
    public ServiceResponse<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServiceResponse<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null, Const.Cart.UN_CHECKED);
    }

    //单独选
    //单独反选
    @RequestMapping("select.do")
    @ResponseBody
    public ServiceResponse<CartVo> Select(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServiceResponse<CartVo> unSelect(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    //查询当前用户的购物车里面的产品数量，如果一个产品有十个，那么数量就是10
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServiceResponse<Integer> getCartProductCount(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }


}
