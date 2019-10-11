package com.lv_mall.service;

import com.lv_mall.common.ServiceResponse;
import com.lv_mall.pojo.User;
public interface IUserService {
    ServiceResponse<User> login(String username, String password);
    ServiceResponse<String> register(User user);
    ServiceResponse<String> checkValid(String str, String type);
    ServiceResponse<String> selectQuestion(String username);
    ServiceResponse<String> checkAnswer(String username, String question, String answer);
    ServiceResponse<String> forgetResetPassword(String username, String forgetToken, String newPassword);
    ServiceResponse<String> resetPassword(String oldPassword, String newPassword, User user);
    ServiceResponse<User> updateInformation(User user);
    ServiceResponse<User> getInformation(Integer userId);
    ServiceResponse checkAdminRole(User user);
}
