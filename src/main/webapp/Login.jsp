<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="./css/account.css">
    <link rel="stylesheet" href="./css/base.css">
</head>
<body>
<%
    String error = (String) request.getAttribute("error");
    String username = request.getParameter("username") != null ? (String) request.getParameter("username") : "";
    String notify = (String) request.getAttribute("notify");
%>
<jsp:include page="header.jsp"/>
<div id="content">
    <div class="form-login">
        <div class="left">
            <img src="./assets/images/account_img/login.png" alt="">
            <p class="title-login">Quyền lợi thành viên</p>
            <ul class="list">
                <li class="list-item"><p>Mua hàng cực dễ dàng, nhanh chóng</p></li>
                <li class="list-item"><p>Theo dõi chi tiết đơn hàng, địa chỉ thanh toán dễ dàng</p></li>
                <li class="list-item"><p>Nhận nhiều chương trình ưu đãi từ chúng tôi</p></li>
            </ul>
        </div>
        <div class="right">
            <div class="function">
                <a class="function-login" href="./login">Đăng nhập</a>
                <a class="function-register" href="./register">Đăng ký</a>
            </div>
            <form class="infor" action="./login" method="post">

                <div class="form-group">
                    <%if (notify != null) {%>
                    <p class="notification-success"><%=notify%>
                    </p>
                    <%}%>
                    <%if (error != null) {%>
                    <p class="notification-error"><%=error%>
                    </p>
                    <%}%>
                    <input type="text" autocomplete="off" name="username" value="<%=username%>" id="username"
                           placeholder="Tên đăng nhập" required="required">
                    <input type="password" name="password" id="password" placeholder="Mật khẩu" required="required">
                </div>

                <div class="g-recaptcha" data-sitekey="6LfsCOYpAAAAAG6HclMXDIeUNArusfVNulX9A_Kb"
                     data-action="LOGIN"></div>
                <div id="error"></div>
                <button type="submit" class="btn_login">Đăng nhập</button>
                <div class="forgot-password"><a href="./forgot">Quên mật khẩu?</a></div>
            </form>

            <div class="third-party-login">
                <a class="loginBtn loginBtn--google"
                   href="https://accounts.google.com/o/oauth2/auth?scope=openid%20profile%20email&redirect_uri=http://localhost:8080/LoginGoogleHandler&response_type=code&client_id=1009898544213-079sof1bo2lnn9a1hr0aaumie5kd7vvs.apps.googleusercontent.com&approval_prompt=force">
                    Login with Google
                </a>
            </div>
            <p class="commit">
                Chúng tôi cam kết bảo mật và không bao giờ đăng hay chia sẻ thông tin mà chưa có được sự đồng ý của bạn
            </p>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<script src="https://www.google.com/recaptcha/api.js"></script>
<script src="https://www.google.com/recaptcha/api.js?render=6LfsCOYpAAAAAG6HclMXDIeUNArusfVNulX9A_Kb"></script>
<script>
    window.onload = function (){
        let isValid = false;
        const form = document.getElementById("form");
        const error = document.getElementById("error");

        form.addEventListener("submit", function (event){
            event.preventDefault();
            const response = grecaptcha.getResponse();
            if (response){
                form.submit();
            } else {
                error.innerHTML = "Please check";
            }
        });
    }
</script>

</body>
</html>