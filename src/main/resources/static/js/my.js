// my.js - Refactored for CampusMarket Spring Boot 3

// 页面加载时的通用操作
$(document).ready(function () {
    // 图片自适应
    $("img").addClass("img-responsive");

    // 返回顶部按钮逻辑
    $(".imgdiv").mouseover(function () {
        $(".imgsrc").attr("src", "/static/images/top4.png");
    });
    $(".imgdiv").mouseout(function () {
        $(".imgsrc").attr("src", "/static/images/top2.png");
    });

    $(window).scroll(function () {
        if ($(window).scrollTop() > 100) {
            $("#gotop").fadeIn(1000);
        } else {
            $("#gotop").fadeOut(1000);
        }
    });

    $("#gotop").click(function () {
        $('body,html').animate({scrollTop: 0}, 1000);
    });
});

// 通用确认框退出登录
function logoutUser() {
    if (confirm("您确定要退出登录吗?")) {
        window.location.href = "/user/logout"; // 对应 Security 默认或 Controller
    }
}

// ---------------------------------------------------------
// 交易与订单 (Trade & Order)
// ---------------------------------------------------------

/**
 * 预订商品 (直接下单)
 * 对应后端: TradeOrderController.create(Integer itemId, HttpSession session)
 */
function reserve(itemId) {
    if (confirm("您确定要预订/购买此商品吗?")) {
        $.ajax({
            url: "/order/create", // 新接口路径
            type: "post",
            data: {itemId: itemId},
            success: function (result) {
                if (result.success) {
                    alert("下单成功！请联系卖家进行线下交易。");
                    // 刷新页面或跳转到订单中心 (假设有)
                    window.location.reload();
                } else {
                    alert(result.errorInfo || "预订失败，请稍后重试");
                }
            },
            error: function() {
                alert("请求失败，请检查登录状态");
            }
        });
    }
}

// ---------------------------------------------------------
// 表单验证 (Validation) - 简化版，移除失效的后端校验接口调用
// ---------------------------------------------------------

// 注册验证
function checkRegisterValue() {
    let password = $("#passwordRegister").val();
    let password2 = $("#password2Register").val();
    let nickName = $("#nickName").val();

    if (password.length < 6) {
        alert("密码长度不能少于6位!");
        return false;
    }
    if (password !== password2) {
        alert("两次输入的密码不一致!");
        return false;
    }
    if (nickName === "") {
        alert("昵称不能为空!");
        return false;
    }
    // 验证码校验逻辑交由后端处理，前端只做非空检查
    return true;
}

// 找回密码验证
function checkResetPasswordValue() {
    let password = $("#passwordResetPassword").val();
    let password2 = $("#password2ResetPassword").val();

    if (password !== password2) {
        alert("两次输入的密码不一致!");
        return false;
    }
    return true;
}

// 修改个人信息验证
function checkModifyValue() {
    let password = $("#passwordModify").val();
    let password2 = $("#password2Modify").val();
    if (password !== "" && password !== password2) { // 允许不修改密码
        alert("两次输入的密码不一致!");
        return false;
    }
    return true;
}

// 留言验证
function checkContactValue() {
    let content = $("#content").val();
    if (content == null || content.trim() === '') {
        alert("留言内容不能为空！");
        return false;
    }
    return true;
}

// 发布/修改商品验证
function checkAddGoodsValue() {
    // 假设使用了 CKEditor
    if (typeof CKEDITOR !== 'undefined' && CKEDITOR.instances.contentGoods) {
        let content = CKEDITO.R.instances.contentGoods.getData();
        if (content === "" || content === null) {
            alert("商品详情不能为空!");
            return false;
        }
    }
    return true;
}

// ---------------------------------------------------------
// 下拉框联动 (如果页面保留了此功能)
// ---------------------------------------------------------
/* 注意：此功能依赖 /getGoodsListByGoodsTypeId 接口。
   如果后端 AdminCategoryController 或 IdleItemController 未提供此接口，
   此函数将失效。根据提供的代码，该接口似乎不存在。
   建议暂时注释掉或确认后端是否补充。
*/
// function getGoodsNameTestPage() { ... }