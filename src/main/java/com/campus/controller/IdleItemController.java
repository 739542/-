package com.campus.controller;

import com.campus.config.AppProperties;
import com.campus.entity.*;
import com.campus.service.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/item")
public class IdleItemController {

    @Resource
    private IdleItemService idleItemService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private UserService userService;
    @Resource
    private UserContactService userContactService;
    @Resource
    private TradeOrderService tradeOrderService;
    @Resource
    private NotificationService notificationService;
    @Resource
    private AppProperties appProperties;

    /**
     * 物品详情页
     */
    @GetMapping("/detail/{id}")
    public ModelAndView detail(@PathVariable("id") Integer id) {
        ModelAndView mav = new ModelAndView("index");
        IdleItem item = idleItemService.findById(id);
        if (item == null) return new ModelAndView("redirect:/");

        item.setViewCount(item.getViewCount() + 1);
        idleItemService.save(item);

        Category category = categoryService.findById(item.getCategoryId());
        item.setCategoryName(category != null ? category.getName() : "未知分类");

        User seller = userService.findById(item.getUserId());
        item.setSeller(seller);

        List<UserContact> contacts = userContactService.findAllByUserId(item.getUserId());
        item.setContactList(contacts);

        TradeOrder order = tradeOrderService.findByItemId(id);
        mav.addObject("tradeOrder", order);

        mav.addObject("categoryList", categoryService.findAll());
        IdleItem recommendFilter = new IdleItem();
        recommendFilter.setStatus(1);
        recommendFilter.setIsRecommended(1);
        mav.addObject("goodsRecommendList", idleItemService.list(1, 10, recommendFilter).getContent());

        mav.addObject("item", item);
        mav.addObject("mainPage", "page/itemDetails");
        mav.addObject("title", item.getName());
        return mav;
    }

    /**
     * 跳转到发布页
     */
    @GetMapping("/publish")
    public ModelAndView toPublish(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        ModelAndView mav = new ModelAndView("index");
        mav.addObject("categoryList", categoryService.findAll());
        mav.addObject("mainPage", "page/publishItem");
        mav.addObject("title", "发布闲置");
        return mav;
    }

    /**
     * 跳转到修改页面
     */
    @GetMapping("/edit/{id}")
    public ModelAndView toEdit(@PathVariable("id") Integer id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        IdleItem item = idleItemService.findById(id);
        if (item == null || !item.getUserId().equals(user.getId())) {
            return new ModelAndView("redirect:/item/my");
        }

        ModelAndView mav = new ModelAndView("index");
        mav.addObject("categoryList", categoryService.findAll());
        mav.addObject("item", item);
        mav.addObject("mainPage", "page/editItem");
        mav.addObject("title", "修改商品");
        return mav;
    }

    /**
     * 保存/更新物品
     */
    @PostMapping("/save")
    public ModelAndView save(IdleItem item,
                             @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                             HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        if (mainImage != null && !mainImage.isEmpty()) {
            try {
                String originalFilename = mainImage.getOriginalFilename();
                String suffixName = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString().replace("-", "") + suffixName;
                File dest = new File(appProperties.getItemImageFilePath() + newFileName);
                if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
                mainImage.transferTo(dest);
                item.setCoverImage(newFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        item.setUserId(user.getId());

        if (item.getId() == null) {
            item.setStatus(0);
            item.setReleaseTime(new Date());
            item.setIsRecommended(0);
            item.setViewCount(0);
        } else {
            IdleItem oldItem = idleItemService.findById(item.getId());
            if (oldItem != null) {
                if (item.getCoverImage() == null) item.setCoverImage(oldItem.getCoverImage());
                item.setReleaseTime(oldItem.getReleaseTime());
                item.setViewCount(oldItem.getViewCount());
                item.setIsRecommended(oldItem.getIsRecommended());
                if (item.getStatus() == null) item.setStatus(oldItem.getStatus());
            }
        }
        idleItemService.save(item);
        return new ModelAndView("redirect:/item/my");
    }

    /**
     * 发送留言
     */
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam("itemId") Integer itemId,
                              @RequestParam("content") String content,
                              HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        IdleItem item = idleItemService.findById(itemId);
        if (item != null) {
            Notification notification = new Notification();
            notification.setUserId(item.getUserId());
            notification.setSenderId(user.getId());
            notification.setItemId(itemId);
            notification.setCreateTime(new Date());
            notification.setIsRead(0);
            notification.setContent(content);
            notificationService.save(notification);
        }
        return "redirect:/item/detail/" + itemId;
    }

    /**
     * 搜索
     */
    @RequestMapping("/search")
    public ModelAndView search(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        ModelAndView mav = new ModelAndView("index");
        IdleItem search = new IdleItem();
        if (keyword != null) search.setName(keyword);
        if (categoryId != null) search.setCategoryId(categoryId);
        search.setStatus(1); // 搜索只显示已上架的

        mav.addObject("itemList", idleItemService.list(1, 20, search).getContent());
        mav.addObject("categoryList", categoryService.findAll());

        IdleItem recommendFilter = new IdleItem();
        recommendFilter.setStatus(1);
        recommendFilter.setIsRecommended(1);
        mav.addObject("goodsRecommendList", idleItemService.list(1, 10, recommendFilter).getContent());

        mav.addObject("keyword", keyword);
        mav.addObject("mainPage", "page/searchResult");
        mav.addObject("title", "搜索结果");
        return mav;
    }

    @GetMapping("/category/{id}")
    public ModelAndView listByCategory(@PathVariable("id") Integer id) {
        return search(null, id);
    }

    /**
     * 我的商品
     */
    @RequestMapping("/my")
    public ModelAndView myItems(HttpSession session, IdleItem search,
                                @RequestParam(value = "page", defaultValue = "1") Integer page) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return new ModelAndView("redirect:/login");

        ModelAndView mav = new ModelAndView("index");
        search.setUserId(user.getId());

        Page<IdleItem> pageResult = idleItemService.list(page, 100, search);
        pageResult.getContent().forEach(item -> {
            Category c = categoryService.findById(item.getCategoryId());
            if (c != null) item.setCategoryName(c.getName());
        });

        mav.addObject("goodsList", pageResult.getContent());
        mav.addObject("goodsTypeList", categoryService.findAll());
        mav.addObject("name", search.getName());
        mav.addObject("categoryId", search.getCategoryId());
        mav.addObject("status", search.getStatus());
        mav.addObject("mainPage", "page/goodsManage");
        mav.addObject("title", "我的商品");
        return mav;
    }

    @RequestMapping("/delete")
    public String delete(Integer id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            IdleItem item = idleItemService.findById(id);
            if (item != null && item.getUserId().equals(user.getId())) {
                idleItemService.delete(id);
            }
        }
        return "redirect:/item/my";
    }

    /**
     * 修改商品状态 (上架/下架/审核通过/驳回)
     */
    @RequestMapping("/updateStatus")
    public String updateStatus(Integer id, Integer status, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            IdleItem item = idleItemService.findById(id);
            if (item != null) {
                // 权限检查：或者是商品主人，或者是管理员(type=1)
                if (item.getUserId().equals(user.getId()) || user.getType() == 1) {
                    item.setStatus(status);
                    idleItemService.save(item);
                }
            }
        }
        // 如果是管理员操作，可能需要返回管理员页面，但这里简化一下，等后面管理员的 controller 调用
        // 如果是用户操作，返回我的商品
        if (user != null && user.getType() == 1) {
            // 这里为了简单，如果管理员是在"我的商品"页面操作的也能生效
            // 后面再改为管理员走 AdminController
            return "redirect:/admin/audit";
        }
        return "redirect:/item/my";
    }

    /**
     * 富文本编辑器图片上传接口
     */
    @PostMapping("/uploadImage")
    public void uploadImage(@RequestParam("upload") MultipartFile file,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            if (file == null || file.isEmpty()) {
                response.getWriter().write("Error: No file uploaded");
                return;
            }
            String originalFilename = file.getOriginalFilename();
            String suffixName = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString().replace("-", "") + suffixName;
            File dest = new File(appProperties.getItemImageFilePath() + newFileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            file.transferTo(dest);
            String url = "/static/images/articleImage/" + newFileName;
            String callback = request.getParameter("CKEditorFuncNum");
            PrintWriter out = response.getWriter();
            if (callback != null) {
                response.setContentType("text/html;charset=UTF-8");
                out.println("<script type=\"text/javascript\">");
                out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ", '" + url + "','');");
                out.println("</script>");
            } else {
                response.setContentType("application/json;charset=UTF-8");
                out.print("{\"uploaded\": 1, \"fileName\": \"" + newFileName + "\", \"url\": \"" + url + "\"}");
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
