<%@ page import="Model.KeyReport" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Account" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <style>
        /* Center table elements and add lines */
        #myTable {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }

        #myTable th, #myTable td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: center;
        }

        #myTable th {
            background-color: #f4f4f4;
            font-weight: bold;
        }

        #myTable tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        #myTable tr:hover {
            background-color: #f5f5f5;
        }

        /* Center the title */
        .title {
            text-align: center;
            margin-bottom: 20px;
        }

        .title h2 {
            margin: 0;
            padding: 20px 0;
        }

        /* Style for the approve button */
        .btn-action {
            padding: 8px 16px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .btn-action:hover {
            background-color: #45a049;
        }

        /* Center the table in container */
        .contain {
            padding: 20px;
        }

        /* DataTables adjustments */
        .dataTables_wrapper {
            padding: 20px 0;
        }

        /* Additional styling for table headers */
        #myTable thead th {
            background-color: #4a5568;
            color: white;
            font-weight: 500;
        }

        /* Status cell styling */
        .status-cell {
            font-weight: 500;
        }

        .status-pending {
            color: #e67e22;
        }

        .status-approved {
            color: #27ae60;
        }

        /* Make the table responsive */
        .table-responsive {
            overflow-x: auto;
            -webkit-overflow-scrolling: touch;
        }

        /* Search box styling */
        .dataTables_filter input {
            padding: 6px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-left: 8px;
        }

        /* Pagination button styling */
        .dataTables_paginate .paginate_button {
            padding: 6px 12px;
            margin: 0 4px;
            border-radius: 4px;
            border: 1px solid #ddd;
            background: #fff;
            cursor: pointer;
        }

        .dataTables_paginate .paginate_button.current {
            background: #4a5568;
            color: white !important;
            border-color: #4a5568;
        }

        /* Table info styling */
        .dataTables_info {
            padding: 10px 0;
            color: #666;
        }
    </style>
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
                    <a href="./managerKeyReports" class="active">
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
                    <h2>Quản lý Key Reports</h2>
                </div>
                <div class="table-responsive">
                    <table id="myTable">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>User ID</th>
                            <th>Username</th>
                            <th>Status</th>
                            <th>Report Date</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${keyReports}" var="report">
                            <tr>
                                <td>${report.id}</td>
                                <td>${report.userID}</td>
                                <td>${report.username}</td>
                                <td class="status-cell ${report.status eq 'PENDING' ? 'status-pending' : 'status-approved'}">
                                        ${report.status}
                                </td>
                                <td>${report.reportDate}</td>
                                <td>
                                    <c:if test="${report.status eq 'PENDING'}">
                                        <form action="./ServletApproveKeyReport" method="post" style="display: inline;">
                                            <input type="hidden" name="reportId" value="${report.id}">
                                            <input type="hidden" name="userId" value="${report.userID}">
                                            <button type="submit" class="btn-action">Approve</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script>
    $(document).ready(function () {
        $('#myTable').DataTable({
            "language": {
                "search": "Tìm kiếm:",
                "lengthMenu": "Hiển thị _MENU_ mục",
                "info": "Hiển thị _START_ đến _END_ của _TOTAL_ mục",
                "infoEmpty": "Hiển thị 0 đến 0 của 0 mục",
                "infoFiltered": "(được lọc từ _MAX_ mục)",
                "paginate": {
                    "first": "Đầu",
                    "last": "Cuối",
                    "next": "Tiếp",
                    "previous": "Trước"
                }
            }
        });
    });
</script>
</html>