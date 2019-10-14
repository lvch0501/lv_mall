package com.lv_mall.controller.backend;


import com.google.common.collect.Maps;
import com.lv_mall.common.Const;
import com.lv_mall.common.ResponseCode;
import com.lv_mall.common.ServiceResponse;
import com.lv_mall.pojo.Product;
import com.lv_mall.pojo.User;
import com.lv_mall.service.IFileService;
import com.lv_mall.service.IProductService;
import com.lv_mall.service.IUserService;
import com.lv_mall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServiceResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充增加产品业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServiceResponse.createByErrorMessage("无操作权限");
        }

    }
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServiceResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //设置商品状态业务逻辑
            return iProductService.setSaleStatus(productId,status);

        }else {
            return ServiceResponse.createByErrorMessage("无操作权限");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse getDetail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.manageProductDetail(productId);

        }else {
            return ServiceResponse.createByErrorMessage("无操作权限");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.getProductList(pageNum, pageSize);

        }else {
            return ServiceResponse.createByErrorMessage("无操作权限");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServiceResponse productSearch(HttpSession session, String productName,Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }else {
            return ServiceResponse.createByErrorMessage("无操作权限");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServiceResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFIleName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFIleName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFIleName);
            fileMap.put("url",url);
            return ServiceResponse.createBySuccess(fileMap);
        }else {
            return ServiceResponse.createByErrorMessage("无操作权限");
        }

    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            resultMap.put("success", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        //富文本中对返回值有自己的要求， 我们使用的是simditor，所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//            "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFIleName = iFileService.upload(file, path);
            if(StringUtils.isBlank(targetFIleName)){
                resultMap.put("success", false);
                resultMap.put("msg", "文件上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFIleName;

            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-FIle-Name");
            return resultMap;
        }else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }

    }

}
