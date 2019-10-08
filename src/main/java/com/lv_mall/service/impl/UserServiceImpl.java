package com.lv_mall.service.impl;

import com.lv_mall.common.Const;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.common.TokenCache;
import com.lv_mall.dao.UserMapper;
import com.lv_mall.pojo.User;
import com.lv_mall.service.IUserService;
import com.lv_mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if(user==null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);

        return ServiceResponse.createBySuccess("登陆成功", user);
    }

    public ServiceResponse<String> register(User user){
        ServiceResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccessMessage("注册成功");
    }

    public ServiceResponse<String> checkValid(String str, String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount>0){
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return ServiceResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else {
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySuccessMessage("校验成功");
    }

    public ServiceResponse<String> selectQuestion(String username){
        ServiceResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNotBlank(question)){
            return ServiceResponse.createBySuccess(question);
        }

        return ServiceResponse.createByErrorMessage("找回密码的问题为空");

    }
    public ServiceResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount>0){
            //说明问题及问题答案是这个用户的，并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误");
    }

    public ServiceResponse<String> forgetResetPassword(String username, String forgetToken, String newPassword){
        if(StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServiceResponse validResponse = this.checkValid(username, Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServiceResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServiceResponse.createByErrorMessage("token无效或者g过期");
        }
        if(StringUtils.equals(forgetToken, token)){
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(rowCount>0){
                return ServiceResponse.createBySuccessMessage("修改密码成功");
            }
        }else {
            return ServiceResponse.createByErrorMessage("token错误，请重新获取重制密码的token");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");

    }

    public ServiceResponse<String> resetPassword(String oldPassword, String newPassword, User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定是这个用户，
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword), user.getId());
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServiceResponse.createBySuccessMessage("密码更改成功");
        }
        return ServiceResponse.createByErrorMessage("更改密码失败");
    }

    public ServiceResponse<User> updateInformation(User user){
        //username不能被更新
        //email也要进行一个校验，校验新的email是否存在，并且存在的话不能是当前用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if(resultCount>0){
            return ServiceResponse.createByErrorMessage("email已经存在");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServiceResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServiceResponse.createByErrorMessage("更新个人信息失败");
    }
}
