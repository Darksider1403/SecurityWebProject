<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="Service.ProductService" %>
<%@ page import="Model.Order_detail" %>
<%@ page import="Service.OrderService" %>
<%@ page import="java.util.List" %>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Order Details</title>
<link rel="stylesheet" href="css/history.css">
<link rel="stylesheet" href="css/user-page.css">
<!-- Include necessary CSS and JS here -->
</head>
<body>
<div class="container1">
    <h1>Chi tiết đơn hàng</h1>
        <%
    String orderIdParam = request.getParameter("orderId");
    Map<String, String> listImagesThumbnail = ProductService.getInstance().selectImageThumbnail();
    if (orderIdParam != null) {
      List<Order_detail> orderDetails = OrderService.getInstance().showOrderDetail(orderIdParam);
  %>
    <table class="table">
        <thead>
        <tr>
            <th></th>
            <th>ID Sản phẩm</th>
            <th>Số lượng</th>
            <th>Giá</th>
        </tr>
        </thead>
        <tbody>
            <% for (Order_detail detail : orderDetails) { %>
        <tr>
            <td>
                <%
                    String productId = detail.getIdProduct();
                    String imageSource = listImagesThumbnail.get(productId);
                %>
                <img src="<%=imageSource%>" alt="">
            </td>
            <td><%= detail.getIdProduct() %></td>
            <td><%= detail.getQuantity() %></td>
            <td><%= detail.getPrice() %></td>
        </tr>
            <% } %>
        </tbody>
    </table>
    <% } else { %>
    <p>Không tìm thấy chi tiết đơn hàng.</p>
    <% } %>
</div>
</body>
</html>