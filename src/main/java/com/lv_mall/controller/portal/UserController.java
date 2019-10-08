package com.lv_mall.controller.portal;

import com.lv_mall.common.Const;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.pojo.User;
import com.lv_mall.service.IUserService;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /**
     * 用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody()
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        //service-->mybatis-->dao
        ServiceResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return  response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.createBySuccess();
    }

    @RequestMapping(value = "register.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }
    @RequestMapping(value = "check_valid.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<String> checkValid(String str, String type){
        return iUserService.checkValid(str, type);
    }
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登陆，无法获取当前用户的信息");
    }

    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<String> forgetCheckAnswer(String username, String question, String answer){
        return iUserService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<String> forgetResetPassword(String username, String forgetToken, String newPassword){
        return iUserService.forgetResetPassword(username,forgetToken,newPassword);
    }

    @RequestMapping(value = "reset_password.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<String> ResetPassword(HttpSession session, String oldPassword, String newPassword){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.resetPassword(oldPassword, newPassword, user);
    }
    @RequestMapping(value = "update_information.do", method = RequestMethod.GET)
    @ResponseBody()
    public ServiceResponse<User> updateInfomation(HttpSession session, User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServiceResponse.createByErrorMessage("用户未登陆");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServiceResponse<User> reponse = iUserService.updateInformation(user);
        if(reponse.isSuccess()){
            reponse.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, reponse.getData());
        }
        return reponse;
    }
}
