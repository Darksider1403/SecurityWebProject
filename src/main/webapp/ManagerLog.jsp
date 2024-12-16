<%@ page import="Model.Log" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Account" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Log Management</title>
    <link rel="stylesheet" href="./css/base.css">
    <link rel="stylesheet" href="./css/admin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"
          integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA=="
          crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="https://cdn.datatables.net/1.11.5/css/jquery.dataTables.min.css">
</head>
<body>
<%
    List<Log> logs = (List<Log>) request.getAttribute("logs");
    Account a = session.getAttribute("account") == null ? new Account() : (Account) session.getAttribute("account");
%>
<div id="id">
    <div id="admin">
        <div class="left">
            <div class="menu">
                <div class="menu-title">
                    <h2 class="shop-name">PLQ SHOP</h2>
                </div>
                <div class="shop-user">
                    <p>Xin chào, <%=a.getFullname()%></p>
                </div>
                <div class="menu-item">
                    <a href="./admin">
                        <div class="icon"><i class="fa-solid fa-house-chimney"></i></div>
                        <p class="menu-content">Thống kê</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerAccount">
                        <div class="icon"><i class="fa-solid fa-desktop"></i></div>
                        <p class="menu-content">Quản lý tài khoản</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerProduct">
                        <div class="icon"><i class="fa-regular fa-calendar-days"></i></div>
                        <p class="menu-content">Quản lý sản phẩm</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerOrder">
                        <div class="icon"><i class="fa-solid fa-clipboard"></i></div>
                        <p class="menu-content">Quản lý đơn hàng</p>
                    </a>
                </div>
                <div class="menu-item" >
                    <a href="./managerLog" class="active">
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
                    <a href="./managerComment?page=1">
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
                    <h2>Quản lý nhật ký</h2>
                </div>
                <table id="logTable" class="display">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>PreAction</th>
                        <th>Action</th>
                        <th>Timestamp</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (Log log : logs) { %>
                    <tr>
                        <td><%= log.getId() %></td>
                        <td><%= log.getAddress() %></td>
                        <td><%= log.getPreValue() %></td>
                        <td><%= log.getValue() %></td>
                        <td><%= log.getDate() %></td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.datatables.net/1.11.5/js/jquery.dataTables.min.js"></script>
<script>
    $(document).ready(function() {
        $('#logTable').DataTable();
    });
</script>
</body>
</html>
