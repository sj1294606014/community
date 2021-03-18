package com.nowcode.community.controller;

import com.nowcode.community.annotation.LoginRequired;
import com.nowcode.community.entity.User;
import com.nowcode.community.service.UserService;
import com.nowcode.community.util.CommunityUtil;
import com.nowcode.community.util.HostHolder;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private  static  final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private  String contextPath;

    @Value("${community.path.domain}")
    private  String domain;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method =RequestMethod.POST )
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage ==null){
            model.addAttribute("error","你还没有选择图片！");
            return "/site/setting";
        }
       String fileName = headerImage.getOriginalFilename();
       String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(suffix ==null){
           model.addAttribute("error","文件格式不正确！") ;
            return "/site/setting";
        }
        //生成随机文件名
        fileName = CommunityUtil.generateUUID()+suffix;
        //确定文件存放路径
        File dest= new File(uploadPath + "/"+fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败！",e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }
        //跟新当前用户头像的路径（web访问的路径）
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl =domain +contextPath +"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }
    @RequestMapping(path = "/header/{fileName}",method =RequestMethod.GET )
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放的路径
        fileName = uploadPath+"/"+fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try (
                FileInputStream fis=new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ){
            byte[] buffer =new byte[1024];
            int b=0;
            while ((b = fis.read(buffer) )!= -1){
                os.write(buffer,0,b);

            }
        } catch (IOException e) {
            logger.error("读取头像失败！",e.getMessage());
        }
    }



}
