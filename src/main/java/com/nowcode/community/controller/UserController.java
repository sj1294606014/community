package com.nowcode.community.controller;

import com.nowcode.community.annotation.LoginRequired;
import com.nowcode.community.entity.Comment;
import com.nowcode.community.entity.DiscussPost;
import com.nowcode.community.entity.Page;
import com.nowcode.community.entity.User;
import com.nowcode.community.service.DiscussPostService;
import com.nowcode.community.service.FollowService;
import com.nowcode.community.service.LikeService;
import com.nowcode.community.service.UserService;
import com.nowcode.community.util.CommunityConstant;
import com.nowcode.community.util.CommunityUtil;
import com.nowcode.community.util.HostHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {

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

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload",method =RequestMethod.POST )
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage ==null){
            model.addAttribute("error","???????????????????????????");
            return "/site/setting";
        }
       String fileName = headerImage.getOriginalFilename();//??????????????????
       String suffix = fileName.substring(fileName.lastIndexOf("."));//??????????????????
        if(suffix ==null){
           model.addAttribute("error","????????????????????????") ;
            return "/site/setting";
        }
        //?????????????????????
        fileName = CommunityUtil.generateUUID()+suffix;
        //????????????????????????
        File dest= new File(uploadPath + "/"+fileName);
        try {
            //????????????
            headerImage.transferTo(dest);//???????????????file
        } catch (IOException e) {
            logger.error("?????????????????????",e.getMessage());
            throw new RuntimeException("?????????????????????????????????????????????",e);
        }
        //????????????????????????????????????web??????????????????
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl =domain +contextPath +"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }
    @RequestMapping(path = "/header/{fileName}",method =RequestMethod.GET )
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //????????????????????????
        fileName = uploadPath+"/"+fileName;
        //????????????
        String suffix = fileName.substring(fileName.lastIndexOf("."));//????????????????????????????????????
        //????????????
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
            logger.error("?????????????????????",e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/updatePassword",method =RequestMethod.POST )
    public String updatePassword( String oldPassword, String newPassword ,Model model){
        User user = hostHolder.getUser();

        String password1 =userService.findUserById(user.getId()).getPassword();
        if(!password1.equals(oldPassword) || StringUtils.isBlank(oldPassword)||StringUtils.isBlank(newPassword)){
            model.addAttribute("passwordMsg","?????????????????????");
        }

        String password =CommunityUtil.md5(newPassword+user.getSalt());
        int row = userService.updatePassword(user.getId(),password);
        System.out.println(row);

        return "redirect:/index";
    }


    //????????????
    //????????????
     @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user ==null){
            throw new RuntimeException("?????????????????????");
        }
        //??????
         model.addAttribute("user",user);

        //????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //????????????
         Long followeeCount = followService.findFolloweeCount(userId,ENTITY_TYPE_USER);
         model.addAttribute("followeeCount",followeeCount);
         //????????????
         Long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER,userId);
         model.addAttribute("followerCount",followerCount);
         //???????????????
         boolean hasFollowed =false;
         if(hostHolder.getUser() != null){
             hasFollowed =followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
         }
        model.addAttribute("hasFollowed",hasFollowed);

        return  "/site/profile";
     }
    //????????????
    @RequestMapping(path ="/mypost/{userId}",method = RequestMethod.GET)
    public String getMyPostPage(@PathVariable("userId") int userId, Page page ,Model model){
       page.setRows(discussPostService.findDiscussPostRows(userId));
       page.setPath(domain+contextPath+"/user/mypost/"+userId);
       page.setLimit(5);

        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPostVo =new ArrayList<>();

        for(DiscussPost discussPost : discussPosts){
            Map<String,Object> map =new HashMap<>();
            map.put("post",discussPost);
            map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST,discussPost.getId()));
            discussPostVo.add(map);
        }
        int postCount = discussPostService.findDiscussPostRows(userId);
        model.addAttribute("postCount",postCount);
        model.addAttribute("discussPosts",discussPostVo);
        model.addAttribute("userId",userId);


        return "/site/my-post";
    }

    //????????????
    @RequestMapping(path ="/myreply/{userId}",method = RequestMethod.GET)
    public String getMyReplyPostPage(@PathVariable("userId") int userId, Page page ,Model model){
        page.setRows(userService.findCommentPostCount(userId));
        page.setPath(domain+contextPath+"/user/myreply/"+userId);
        page.setLimit(10);

        List<Comment> commentPosts = userService.findCommentPost(userId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> replys =new ArrayList<>();

        for(Comment comment : commentPosts){

            Map<String,Object> map =new HashMap<>();
            map.put("reply",comment);
            replys.add(map);
        }
        int postCount = discussPostService.findDiscussPostRows(userId);
        model.addAttribute("postCount",userService.findCommentPostCount(userId));
        model.addAttribute("replys",replys);
        model.addAttribute("userId",userId);


        return "/site/my-reply";
    }




}
