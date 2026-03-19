package com.campus.controller;

import com.campus.config.AppProperties;
import com.campus.entity.User;
import com.campus.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AppProperties appProperties;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @PostMapping("/login")
    public ModelAndView login(User user, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        User dbUser = userService.findByUsername(user.getUsername());

        if (dbUser != null && dbUser.getPassword().equals(user.getPassword())) {
            if (dbUser.getStatus() == 0) {
                mav.addObject("errorInfo", "账号已被封禁");
                mav.setViewName("index");
                mav.addObject("mainPage", "page/login");
            } else {
                session.setAttribute("currentUser", dbUser);
                mav.setViewName("redirect:/");
            }
        } else {
            mav.addObject("errorInfo", "用户名或密码错误");
            mav.setViewName("index");
            mav.addObject("mainPage", "page/login");
        }
        return mav;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("currentUser");
        return "redirect:/login";
    }

    /**
     * 发送注册验证码
     */
    @PostMapping("/sendRegisterCode")
    @ResponseBody
    public Map<String, Object> sendRegisterCode(String email, HttpSession session) {
        Map<String, Object> map = new HashMap<>();

        if (email == null || email.isEmpty()) {
            map.put("success", false);
            map.put("errorInfo", "邮箱不能为空");
            return map;
        }

        // 生成验证码
        String code = String.valueOf(new Random().nextInt(899999) + 100000);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("【校园闲置易】注册验证码");
            message.setText("欢迎注册！您的验证码是：" + code + "。请在5分钟内完成注册。");
            mailSender.send(message);

            // 存入 Session，Key 区别于找回密码
            session.setAttribute("REGISTER_CODE", code);
            session.setAttribute("REGISTER_EMAIL", email); // 绑定邮箱防止篡改

            map.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errorInfo", "发送失败，请检查邮箱地址");
        }
        return map;
    }

    /**
     * 用户注册 (增加了验证码校验逻辑)
     */
    @PostMapping("/register")
    public ModelAndView register(User user,
                                 @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                 @RequestParam(value = "emailCode", required = false) String emailCode, // 接收前端传来的验证码
                                 HttpSession session) {
        ModelAndView mav = new ModelAndView("index"); // 默认视图，如果失败则返回注册页

        // 校验验证码
        String correctCode = (String) session.getAttribute("REGISTER_CODE");
        String correctEmail = (String) session.getAttribute("REGISTER_EMAIL");

        if (emailCode == null || !emailCode.equals(correctCode)) {
            mav.addObject("errorInfo", "验证码错误或已失效");
            mav.addObject("mainPage", "page/register");
            return mav;
        }
        if (!user.getEmail().equals(correctEmail)) {
            mav.addObject("errorInfo", "提交的邮箱与接收验证码的邮箱不一致");
            mav.addObject("mainPage", "page/register");
            return mav;
        }
        // -------------------

        try {
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String originalFilename = avatarFile.getOriginalFilename();
                String suffixName = null;
                if (originalFilename != null) {
                    suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString().replace("-", "") + suffixName;

                File dest = new File(appProperties.getUserImageFilePath() + newFileName);
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                avatarFile.transferTo(dest);
                user.setImageName(newFileName);
            } else {
                user.setImageName("default.jpg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        user.setType(2);
        user.setStatus(1);
        userService.save(user);

        // 注册成功后清除 Session
        session.removeAttribute("REGISTER_CODE");
        session.removeAttribute("REGISTER_EMAIL");

        return new ModelAndView("redirect:/login");
    }

    @PostMapping("/update")
    public ModelAndView update(User user,
                               @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                               HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return new ModelAndView("redirect:/login");

        User dbUser = userService.findById(currentUser.getId());
        if (user.getNickName() != null && !user.getNickName().isEmpty()) {
            dbUser.setNickName(user.getNickName());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            dbUser.setPassword(user.getPassword());
        }
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String originalFilename = avatarFile.getOriginalFilename();
                String suffixName = "";
                if (originalFilename != null && originalFilename.lastIndexOf(".") != -1) {
                    suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString().replace("-", "") + suffixName;
                File dest = new File(appProperties.getUserImageFilePath() + newFileName);
                if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
                avatarFile.transferTo(dest);
                dbUser.setImageName(newFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        userService.save(dbUser);
        session.setAttribute("currentUser", dbUser);
        return new ModelAndView("redirect:/user/info");
    }

    @RequestMapping("/center")
    public ModelAndView center(HttpSession session) {
        if(session.getAttribute("currentUser") == null) return new ModelAndView("redirect:/login");
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("mainPage", "page/personalHubs");
        mav.addObject("title", "个人中心");
        return mav;
    }

    @RequestMapping("/info")
    public ModelAndView info(HttpSession session) {
        if(session.getAttribute("currentUser") == null) return new ModelAndView("redirect:/login");
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("mainPage", "page/personalInfo");
        mav.addObject("title", "修改个人信息");
        return mav;
    }

    @RequestMapping("/forget")
    public ModelAndView toForget() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("mainPage", "page/forgetPassword");
        mav.addObject("title", "找回密码");
        return mav;
    }

    @PostMapping("/sendResetCode")
    @ResponseBody
    public Map<String, Object> sendResetCode(String username, String email, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        User user = userService.findByUsername(username);
        if (user == null) {
            map.put("success", false);
            map.put("errorInfo", "用户名不存在");
            return map;
        }
        if (user.getEmail() == null || !user.getEmail().equals(email)) {
            map.put("success", false);
            map.put("errorInfo", "该用户名未绑定此邮箱，无法验证");
            return map;
        }
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("【校园闲置易】找回密码验证");
            message.setText("您的验证码是：" + code + "。请在5分钟内使用，切勿泄露给他人。");
            mailSender.send(message);
            session.setAttribute("RESET_CODE", code);
            session.setAttribute("RESET_USER_ID", user.getId());
            map.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("errorInfo", "邮件发送失败，请检查邮箱地址或联系管理员");
        }
        return map;
    }

    @PostMapping("/doResetPassword")
    @ResponseBody
    public Map<String, Object> doResetPassword(String verifyCode, String newPassword, HttpSession session) {
        Map<String, Object> map = new HashMap<>();
        String correctCode = (String) session.getAttribute("RESET_CODE");
        Integer userId = (Integer) session.getAttribute("RESET_USER_ID");

        if (correctCode == null || userId == null) {
            map.put("success", false);
            map.put("errorInfo", "验证码已过期，请重新发送");
            return map;
        }
        if (!correctCode.equals(verifyCode)) {
            map.put("success", false);
            map.put("errorInfo", "验证码错误");
            return map;
        }
        User user = userService.findById(userId);
        if (user != null) {
            user.setPassword(newPassword);
            userService.save(user);
            session.removeAttribute("RESET_CODE");
            session.removeAttribute("RESET_USER_ID");
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("errorInfo", "用户异常");
        }
        return map;
    }
}