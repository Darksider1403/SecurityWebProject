<%@ page import="java.util.List" %>
<%@ page import="Model.Account" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
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
<%
    Account a = session.getAttribute("account") == null ? new Account() : (Account) session.getAttribute("account");
    int totalPage = request.getAttribute("totalPage") == null ? 0 : (int) request.getAttribute("totalPage");
    int pageCurrent = request.getAttribute("pageCurrent") == null ? 1 : Integer.parseInt(request.getAttribute("pageCurrent").toString());
    String search = request.getAttribute("search") == null ? "" : "&search=" + request.getAttribute("search").toString();
    List<Account> accountList = request.getAttribute("accountList") == null ? new ArrayList<>() : (List<Account>) request.getAttribute("accountList");
%>
<body>
<div id="id">
    <div id="admin">
        <div class="left">
            <div class="menu">
                <div class="menu-title">
                    <h2 class="shop-name">PLQ SHOP</h2>
                </div>
                <div class="shop-user">
                    <p>Xin chào, <%=a.getFullname()%>
                    </p>
                </div>
                <div class="menu-item">
                    <a href="./admin">
                        <div class="icon"><i class="fa-solid fa-house-chimney"></i></div>
                        <p class="menu-content">Thống kê</p>
                    </a>
                </div>
                <div class="menu-item">
                    <a href="./managerAccount" class="active">
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
                    <h2>Quản lý tài khoản</h2>
                </div>
                <div class="manager">
                    <div class="manager-infor">
                        <table id="myTable">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên đăng nhập</th>
                                <th>Email</th>
                                <th>Họ và tên</th>
                                <th>Số điện thoại</th>
                                <th>Quyền</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <% for (Account account : accountList) {%>
                            <tr>
                                <th><%=account.getID()%>
                                </th>
                                <th><%=account.getUsername()%>
                                </th>
                                <th><%=account.getEmail()%>
                                </th>
                                <th><%=account.getFullname()%>
                                </th>
                                <%
                                    if (account.getNumberPhone().equals("0")) {
                                        account.setNumberPhone("Chưa cập nhật");
                                    }
                                %>
                                <th><%=account.getNumberPhone()%>
                                </th>
                                <form action="./managerAccount" method="post">
                                    <input type="hidden" name="idAccount" value="<%=account.getID()%>">
                                    <th>
                                        <label>
                                            <select class="role" name="role">
                                                <% if (account.getRole() == 1) {%>
                                                <option value="1" selected>Người dùng</option>
                                                <option value="2">Quản lý đơn hàng</option>
                                                <option value="3">Quản lí sản phẩm</option>
                                                <option value="0">Admin</option>
                                                <%} else if (account.getRole() == 2) {%>
                                                <option value="1">Người dùng</option>
                                                <option value="2" selected>Quản lý đơn hàng</option>
                                                <option value="3">Quản lí sản phẩm</option>
                                                <option value="0">Admin</option>
                                                <%} else if (account.getRole() == 3) {%>
                                                <option value="1">Người dùng</option>
                                                <option value="2">Quản lý đơn hàng</option>
                                                <option value="3" selected>Quản lí sản phẩm</option>
                                                <option value="0">Admin</option>
                                                <%} else if (account.getRole() == 0) {%>
                                                <option value="1">Người dùng</option>
                                                <option value="2">Quản lý đơn hàng</option>
                                                <option value="3">Quản lí sản phẩm</option>
                                                <option value="0" selected>Admin</option>
                                                <%}%>
                                            </select>
                                        </label>
                                    </th>
                                    <th>
                                        <select class="status" name="status">
                                            <% if (account.getStatus() == 1) {%>
                                            <option value="1" selected>Bình thường</option>
                                            <option value="2">Đã bị khóa</option>
                                            <%} else if (account.getStatus() == 2) {%>
                                            <option value="1">Bình thường</option>
                                            <option value="2" selected>Đã bị khóa</option>
                                            <%}%>
                                        </select>
                                    </th>

                                    <th>
                                        <button type="submit" class="btn-repair">Lưu</button>
                                    </th>
                                </form>
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
<script src="./js/account.js"></script>
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
