<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="Model.Account" %>
<%@ page import="Service.ProductService" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="Model.Comment" %>
<%@ page import="Service.FeedbackAndRatingService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link rel="stylesheet" href="./css/base.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"
          integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <link rel="stylesheet" href="./css/admin.css">
    <link rel="stylesheet" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.min.css">
    <script src="https://code.jquery.com/jquery-3.5.1.js"></script>
    <script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
</head>
<body>
<%
    Comment comment = request.getAttribute("comment") == null ? new Comment() : (Comment) request.getAttribute("comment");
    Account account = session.getAttribute("account") == null ? new Account() : (Account) session.getAttribute("account");
    String id = request.getAttribute("id") == null ? "" : request.getAttribute("id").toString();
    int totalComment = request.getAttribute("totalComment") == null ? 0 : (int) request.getAttribute("totalComment");
    int totalPage = request.getAttribute("totalPage") == null ? 0 : (int) request.getAttribute("totalPage");
    int pageCurrent = request.getAttribute("pageCurrent") == null ? 1 : Integer.parseInt(request.getAttribute("pageCurrent").toString());
    String search = request.getAttribute("search") == null ? "" : "&search=" + request.getAttribute("search").toString();
    List<Comment> commentList = request.getAttribute("commentList") == null ? new ArrayList<>() : (List<Comment>) request.getAttribute("commentList");
    FeedbackAndRatingService feedbackAndRatingService =
            request.getAttribute("ps") == null
                    ? FeedbackAndRatingService.getInstance()
                    : (FeedbackAndRatingService) request.getAttribute("feedbackAndRatingService");
    List<Account> accountList = request.getAttribute("accountList") == null ? new ArrayList<>() : (List<Account>) request.getAttribute("accountList");
    System.out.println(commentList.size());
    NumberFormat nf = NumberFormat.getInstance();
%>
<div id="id">
    <div id="admin">
        <div class="left">
            <div class="menu">
                <div class="menu-title">
                    <h2 class="shop-name">PLQ SHOP</h2>
                </div>
                <div class="shop-user">
                    <p>Xin chào, <%=account.getFullname()%>
                    </p>
                </div>
                <div class="menu-item">
                    <a href="./admin">
                        <div class="icon"><i class="fa-solid fa-house-chimney"></i></div>
                        <p class="menu-content">Thống kê</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerAccount?page=1">
                        <div class="icon"><i class="fa-solid fa-desktop"></i></div>
                        <p class="menu-content">Quản lý tài khoản</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerProduct?page=1">
                        <div class="icon"><i class="fa-regular fa-calendar-days"></i></div>
                        <p class="menu-content">Quản lý sản phẩm</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerOrder?page=1">
                        <div class="icon"><i class="fa-solid fa-clipboard"></i></div>
                        <p class="menu-content">Quản lý đơn hàng</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerLog">
                        <div class="icon"><i class="fa-solid fa-file-alt"></i></div>
                        <p class="menu-content">Quản lý nhật ký</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./createVoucher?page=1">
                        <div class="icon"><i class="fa-solid fa-gift"></i></div>
                        <p class="menu-content">Quản lý Voucher</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerComment?page=1" class="active">
                        <div class="icon"><i class="fa-solid fa-comment"></i></div>
                        <p class="menu-content">Quản lý bình luận</p>
                    </a>
                </div>

                <div class="menu-item">
                    <a href="./managerKeyReports">
                        <div class="icon"><i class="fa-solid fa-key"></i></div>
                        <p class="menu-content">Quản lý Key Reports</p>
                    </a>
                </div>

                <div class="menu-item">
                    <a href="./ServletLogOut">
                        <p class="menu-content">Đăng xuất</p>
                    </a>
                </div>
            </div>
        </div>
        <div class="right">
            <div class="contain">
                <div class="title">
                    <h2>Quản lý bình luận</h2>
                </div>
                <div class="manager">
                    <div class="manager-infor">
                        <table id="myTable">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên tài khoản</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Nội dung bình luận</th>
                                <th>Ngày bình luận</th>
                                <th>ID sản phẩm</th>
<%--                                <th>Thao tác</th>--%>
                            </tr>
                            </thead>
                            <tbody>
                            <% for (Comment c : commentList) {
                                if (c.getStatus() == 0) {
                                    System.out.println(commentList);
                                }
                            %>
                            <% String numberPhone = !c.getNumberPhone().equals("0") ? c.getNumberPhone() : "Chưa cập nhật"; %>
                            <tr>
                                <th><%=c.getId()%></th>
                                <th><%=c.getUsername()%>
                                </th>
                                <th><%=c.getEmail()%>
                                </th>
                                <th><%=numberPhone%></th>
                                <th><%=c.getContent()%>
                                </th>
                                <th><%=c.getDateComment()%>
                                </th>
<%--                                <th>--%>
<%--                                    <select class="status" name="status">--%>
<%--                                        <% if (c.getStatus() == 1) {%>--%>
<%--                                        <option value="1" selected>Ẩn bình luận</option>--%>
<%--                                        <option value="2">Bình thường</option>--%>
<%--                                        <%} else if (c.getStatus() == 2) {%>--%>
<%--                                        <option value="1">Ẩn bình luận</option>--%>
<%--                                        <option value="2" selected>Bình thường</option>--%>
<%--                                        <%}%>--%>
<%--                                    </select>--%>
<%--                                </th>--%>
                                <th><%=c.getIdProduct()%> </th>
                            </tr>
                            <%}%>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    function openModal() {
        document.getElementById("myModal").style.display = "flex";
    }

    function closeModal() {
        document.getElementById("myModal").style.display = "none";
    }

    $(document).ready(function () {
        $('#myTable').DataTable();
    })
</script>
</html>
