<%@ page import="Model.Account" %>
<%@ page import="Model.Slider" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.ShoppingCart" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="css/base.css">
    <link rel="stylesheet" href="css/header.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
          integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A=="
          crossorigin="anonymous" referrerpolicy="no-referrer">
    <script src="https://code.jquery.com/jquery-3.5.1.js"></script>
</head>
<body>
<%
    Account account = (Account) session.getAttribute("account");
    List<Slider> sliders = (List<Slider>) request.getAttribute("slider");
    ShoppingCart gh = (ShoppingCart) session.getAttribute("cart");
    if (gh == null) gh = new ShoppingCart();
%>
<div id="header">
    <div class="container">
        <nav>
            <ul class="header__list-item">
                <li class="header__item"><a href="./home">Trang chủ</a></li>
                <li class="header__item"><a href="./product?category=1&page=1">Danh sách sản phẩm</a></li>
            </ul>

            <div class="header-contain__search">
                <input class="header__search" oninput="searchProduct(this)" name="search" placeholder="Tìm kiếm sản phẩm" type="text">
                <i class="fa-solid fa-magnifying-glass"></i>
                <div id="header-contain__display-product">
                    <ul id="header__list-products">

                    </ul>
                </div>
            </div>
            <ul class="list-item">
                <li class="item"><a href="./signature">Kí Tên Điện Tử</a></li>
            </ul>
            <div class="header-contain__method">
                <a href="./CartServlet" class="header_cart">
                    <i class="fa fa-fw fa-cart-arrow-down mr-1"></i>
                    <span class="position-absolute left-100 translate-middle badge bg-light text-dark"
                          style="border-radius: 30rem !important;" id="cart-size">
                        <%= gh.getSize() %>
                    </span>
                </a>
                <% if (session.getAttribute("account") == null) { %>
                <a class="nav-icon position-relative text-decoration-none header__login" href="./login">
                    <i class="fa fa-fw fa-user mr-3"></i>
                </a>
                <% } else { %>
                <a class="nav-icon position-relative text-decoration-none header__login" href="users-page.jsp">
                    <i class="fa fa-fw fa-user mr-3"></i>
                </a>
                <% } %>
            </div>
        </nav>
    </div>
</div>

<script>
    function updateCartSize() {
        $.ajax({
            url: './CartSizeServlet',
            method: 'GET',
            success: function (response) {
                $('#cart-size').text(response.cartSize);
            }
        });
    }

    $(document).ready(function () {
        // Call updateCartSize periodically or after adding an item to the cart
        updateCartSize();
    });
</script>
</body>

<script>
    function searchProduct(input) {
        let content = input.value;
        let displaySearch = document.getElementById("header-contain__display-product");
        if (content == "") {
            displaySearch.style.display = "none"
        } else {
            displaySearch.style.display = "block"
        }
        console.log(content)
        $.ajax({
            url: "search",
            type: "POST",
            data: {
                content: content
            },
            success: function (data) {
                let listProduct = document.getElementById("header__list-products");
                listProduct.innerHTML = data;
            }
        })
    }
</script>
</html>

