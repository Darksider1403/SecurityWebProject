<%@ page import="java.util.List" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="Model.Product" %>
<%@ page import="Model.*" %>

<%
    Account account = (Account) session.getAttribute("account");
    List<Slider> sliders = (List<Slider>) request.getAttribute("slider");
    ShoppingCart gh = (ShoppingCart) session.getAttribute("cart");
    if (gh == null) gh = new ShoppingCart();
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <!--   slick-->
    <link rel="stylesheet" href="css/base.css">
    <link rel="stylesheet" href="css/home.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
          integrity="sha512-SnH5WK+bZxgPHs44uWIX+LLJAJ9/2PkPKZ5QiAj6Ta86w+fsb2TkcmfRyVX3pBnMFcV7oQPJkl9QevSCWr3W6A=="
          crossorigin="anonymous" referrerpolicy="no-referrer">



    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.9.0/slick.min.css"
          integrity="sha512-yHknP1/AwR+yx26cB1y0cjvQUMvEa2PFzt1c9LlS4pRQ5NOTZFWbhBig+X9G9eYW/8m0/4OXNx8pxJ6z57x0dw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.9.0/slick-theme.min.css"
          integrity="sha512-17EgCFERpgZKcm0j0fEq1YCJuyAWdz9KUtv1EjVuaOz8pDnh/0nZxmU6BBXwaaxqoi9PQXnRWqlcDB027hgv9A=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>


</head>
<body>
<jsp:include page="header.jsp"/>
    <% if (sliders != null && !sliders.isEmpty()) { %>
<div id="carouselExampleIndicators" class="carousel slide" style="padding-bottom: 15px" data-bs-ride="carousel">
    <div class="carousel-inner">
        <% for (int i = 0; i < sliders.size(); i++) {
            Slider slider = sliders.get(i);%>
        <div class="carousel-item <%= i == 0 ? " active" : "" %>">
            <img src="<%= slider.getSource() %>" class="d-block w-100" alt="...">
        </div>
        <% } %>
    </div>
    <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleIndicators"
            data-bs-slide="prev">
        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
    </button>
    <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleIndicators"
            data-bs-slide="next">
        <span class="carousel-control-next-icon" aria-hidden="true"></span>
    </button>
</div>
    <% } else { %>
<p>No slider available.</p>
    <% } %>
</section>
    <%
    List<Product> productListNam = request.getAttribute("listProduct") == null ? new ArrayList<>() : (List<Product>) request.getAttribute("listProduct");
    NumberFormat nf = NumberFormat.getInstance();
    Map<String, String> listImagesThumbnail = request.getAttribute("listImagesThumbnail") == null ? new HashMap<>() : (Map<String, String>) request.getAttribute("listImagesThumbnail");
%>

<div class="container" id="Nam-Container">
    <div class="top-prodcut">
        <div class="title">Thắt Lưng Da</div>
        <div class="menu-item">
            <div class="menu-item"><a href="./product?category=1&page=1">Xem tất cả</a></div>
        </div>
    </div>
    <div class="bottom-product">
        <div class="left-menu-item">
            <img src="assets/images/thatlungDa.jpg" alt="">
        </div>
        <div class="right-menu-list">
            <div class="slider-product">

                    <% for (Product product : productListNam) {
                    %>
                <div class="product-item-home">
                    <div class="product">

                        <a href="productDetail?id=<%= product.getId() %>"><img class="product-img"
                                                                               style="width: 270px;height: 300px"
                                                                               src="<%=listImagesThumbnail.get(product.getId())%>"
                                                                               alt=""></a>
                        <p class="product-title">
                            <%= product.getName().length() > 20 ? product.getName().substring(0, 20) + "..." : product.getName() %>
                        </p>
                        <div class="product-detail">
                            <p class="product-price"><%= nf.format(product.getPrice()) %>đ</p>
                            <div class="order">
                                <button onclick="addToCart('<%= product.getId() %>')">
                                    <i class="fa-solid fa-shopping-cart"></i>
                                </button>
                            </div>
                            <span class="rating">
        <span class="rating-value"></span>
        <i class="fa-solid fa-star"></i>
    </span>
                        </div>


                        <a href="productDetail?id=<%= product.getId() %>" class="product-order">Xem chi tiết</a>

                    </div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>
    <%
   List<Product> productVai = request.getAttribute("listProductVai") == null ? new ArrayList<>() : (List<Product>) request.getAttribute("listProductVai");
%>

<div class="container" id="Nam-Container">
    <div class="top-prodcut">
        <div class="title">Khẩu trang</div>
        <div class="menu-item">
            <div class="menu-item"><a href="./product?category=1&page=1">Xem tất cả</a></div>
        </div>
    </div>
    <div class="bottom-product">
        <div class="left-menu-item">
            <img src="assets/images/thatlungVai.jpg" alt="">
        </div>
        <div class="right-menu-list">
            <div class="slider-product">
                    <% for (Product product : productVai) { %>
                <div class="product-item-home">
                    <div class="product">
                        <a href="productDetail?id=<%= product.getId() %>"><img class="product-img"
                                                                               style="width: 270px;height: 300px"
                                                                               src="<%=listImagesThumbnail.get(product.getId())%>"
                                                                               alt=""></a>
                        <p class="product-title">
                            <%= product.getName().length() > 20 ? product.getName().substring(0, 20) + "..." : product.getName() %>
                        </p>
                        <div class="product-detail">
                            <p class="product-price"><%= nf.format(product.getPrice()) %>đ</p>
                            <div class="order">
                                <button onclick="addToCart('<%= product.getId() %>')">
                                    <i class="fa-solid fa-shopping-cart"></i>
                                </button>
                            </div>
                            <span class="rating">
        <span class="rating-value"></span>
        <i class="fa-solid fa-star"></i>
    </span>
                        </div>
                        <a href="productDetail?id=<%= product.getId() %>" class="product-order">Xem chi tiết</a>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>
    <%
   List<Product> Nu = request.getAttribute("productNu") == null ? new ArrayList<>() : (List<Product>) request.getAttribute("productNu");
%>
<div class="container" id="Nam-Container">
    <div class="top-prodcut">
        <div class="title">Nữ</div>
        <div class="menu-item">
            <div class="menu-item"><a href="./product?category=1&page=1">Xem tất cả</a></div>
        </div>
    </div>
    <div class="bottom-product">
        <div class="left-menu-item">
            <img src="assets/images/thatlungnu.jpg" alt="">
        </div>
        <div class="right-menu-list">
            <div class="slider-product">
                    <% for (Product product : Nu) { %>
                <div class="product-item-home">
                    <div class="product">
                        <a href="productDetail?id=<%= product.getId() %>"><img class="product-img"
                                                                               style="width: 270px;height: 300px"
                                                                               src="<%=listImagesThumbnail.get(product.getId())%>"
                                                                               alt=""></a>
                        <p class="product-title">
                            <%= product.getName().length() > 20 ? product.getName().substring(0, 20) + "..." : product.getName() %>
                        </p>
                        <div class="product-detail">
                            <p class="product-price"><%= nf.format(product.getPrice()) %>đ</p>
                            <div class="order">
                                <button onclick="addToCart('<%= product.getId() %>')">
                                    <i class="fa-solid fa-shopping-cart"></i>
                                </button>
                            </div>
                            <span class="rating">
        <span class="rating-value"></span>
        <i class="fa-solid fa-star"></i>
    </span>
                        </div>
                        <a href="productDetail?id=<%= product.getId() %>" class="product-order">Xem chi tiết</a>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>
    <%
   List<Product> Nam = request.getAttribute("productNu") == null ? new ArrayList<>() : (List<Product>) request.getAttribute("productNam");
%>
<div class="container" id="Nam-Container">
    <div class="top-prodcut">
        <div class="title">Nam</div>
        <div class="menu-item">
            <div class="menu-item"><a href="./product?category=1&page=1">Xem tất cả</a></div>
        </div>
    </div>
    <div class="bottom-product">
        <div class="left-menu-item">
            <img src="assets/images/nam1.jpg" alt="">
        </div>
        <div class="right-menu-list">
            <div class="slider-product">
                    <% for (Product product : Nam) { %>
                <div class="product-item-home">
                    <div class="product">
                        <a href="productDetail?id=<%= product.getId() %>"><img class="product-img"
                                                                               style="width: 270px;height: 300px"
                                                                               src="<%=listImagesThumbnail.get(product.getId())%>"
                                                                               alt=""></a>
                        <p class="product-title">
                            <%= product.getName().length() > 20 ? product.getName().substring(0, 20) + "..." : product.getName() %>
                        </p>
                        <div class="product-detail">
                            <p class="product-price"><%= nf.format(product.getPrice()) %>đ</p>
                            <div class="order">
                                <button onclick="addToCart('<%= product.getId() %>')">
                                    <i class="fa-solid fa-shopping-cart"></i>
                                </button>
                            </div>
                            <span class="rating">
        <span class="rating-value"></span>
        <i class="fa-solid fa-star"></i>
    </span>
                        </div>
                        <a href="productDetail?id=<%= product.getId() %>" class="product-order">Xem chi tiết</a>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.9.0/slick.min.js"
        integrity="sha512-HGOnQO9+SP1V92SrtZfjqxxtLmVzqZpjFFekvzZVWoiASSQgSr4cw9Kqd2+l8Llp4Gm0G8GIFJ4ddwZilcdb8A=="
        crossorigin="anonymous" referrerpolicy="no-referrer"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM"
        crossorigin="anonymous"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        document.querySelectorAll(".btn-add-to-cart").forEach(function(button) {
            button.addEventListener("click", function() {
                const productId = this.getAttribute("data-id");
                addToCart(productId);
            });
        });
    });
    function addToCart(productId) {
        $.ajax({
            url: './AddToCartServlet',
            method: 'POST',
            data: { masanpham: productId },
            success: function (response) {
                if (response.success) {
                    $('#cart-size').text(response.cartSize);
                } else {alert("Failed to add item to cart.");
                } }
        });
    }function updateCartSize() {
        $.ajax({
            url: './CartSizeServlet',
            method: 'GET',
            success: function (response) {
                $('#cart-size').text(response.cartSize);
            }
        });
    }

    $(document).ready(function () {
        // Initial call to set the cart size on page load
        updateCartSize();
    });



    $('.slider-product').slick({
        dots: true,
        infinite: false,
        speed: 300,
        slidesToShow: 3,
        slidesToScroll: 3,
        responsive: [
            {
                breakpoint: 1024,
                settings: {
                    slidesToShow: 3,
                    slidesToScroll: 3,
                    infinite: true,
                    dots: true
                }
            },
            {
                breakpoint: 600,
                settings: {
                    slidesToShow: 2,
                    slidesToScroll: 2
                }
            },
            {
                breakpoint: 480,
                settings: {
                    slidesToShow: 1,
                    slidesToScroll: 1
                }
            }
        ]
    });

</script>
<jsp:include page="footer.jsp"/>
</body>
</html>