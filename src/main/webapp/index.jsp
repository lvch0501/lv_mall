<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<html>
<body>
<h2>Hello World!111</h2>
springmvc 文件上传
<form name="form1" action="manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="springmvc文件上传">
</form>
富文本图片上传
<form name="form1" action="manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="富文本图片上传">
</form>
</body>
</html>
