package com.campus.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码控制器
 */
@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @GetMapping("/generate")
    public void generate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        int width = 100, height = 30;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 简单的验证码生成逻辑
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, width, height);

        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(chars.charAt(random.nextInt(chars.length())));
            sb.append(rand);
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(rand, 13 * i + 20, 20);
        }

        // 存入 Session
        HttpSession session = request.getSession();
        session.setAttribute("checkCode", sb.toString());

        g.dispose();
        ImageIO.write(image, "JPEG", response.getOutputStream());
    }
}