package com.lv_mall.controller.backend;


import com.lv_mall.common.Const;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.pojo.User;
import com.lv_mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager/user")
public class UserManagerController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        ServiceResponse<User> reponse = iUserService.login(username,password);
        if (reponse.isSuccess()){
            User user = reponse.getData();
            if(user.getRole()== Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER, user);
                return reponse;
            }else {
                return ServiceResponse.createByErrorMessage("不是管理员，无法登陆");
            }
        }
        return reponse;
    }



}
