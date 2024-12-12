<%@ page import="Model.Product" %>
<%@ page import="Service.ProductService" %>
<%@ page import="java.util.*" %>
<%@ page import="Model.Account" %>
<%@ page import="Model.Comment" %>
<%@ page import="Service.FeedbackAndRatingService" %>
<%@ page import="DAO.AccountDAO" %>
<%@ page import="Service.AccountService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Document</title>
    <link
            href="https://fonts.googleapis.com/css2?family=Roboto:wght@100;300;400;500;700&display=swap"
            rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.9.0/slick-theme.min.css"
          integrity="sha512-17EgCFERpgZKcm0j0fEq1YCJuyAWdz9KUtv1EjVuaOz8pDnh/0nZxmU6BBXwaaxqoi9PQXnRWqlcDB027hgv9A=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.9.0/slick.min.css"
          integrity="sha512-yHknP1/AwR+yx26cB1y0cjvQUMvEa2PFzt1c9LlS4pRQ5NOTZFWbhBig+X9G9eYW/8m0/4OXNx8pxJ6z57x0dw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
            rel="stylesheet"
            integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
            crossorigin="anonymous">
    <link rel="stylesheet" href="css/productDetail.css">
</head>

<body>
<jsp:include page="header.jsp"/>
<% Account account = (Account) session.getAttribute("account");
    Double
            productRating = (Double) request.getAttribute("productRating");
    ProductService
            productService = request.getAttribute("ps") == null ?
            ProductService.getInstance() : (ProductService) request.getAttribute("ps");
    FeedbackAndRatingService
            feedbackAndRatingService = request.getAttribute("feedbackAndRatingService") == null
            ? FeedbackAndRatingService.getInstance() : (FeedbackAndRatingService)
            request.getAttribute("feedbackAndRatingService");
    Product
            selectedProduct = (Product) request.getAttribute("selectedProduct");
    String
            productId = selectedProduct != null ? selectedProduct.getId() : "";
    List<Comment> comments =
            feedbackAndRatingService.getCommentsByProductId(productId);
    Map<String, String> imageMap =
            productService.selectImageProductDetail(selectedProduct != null ?
                    selectedProduct.getId() : null);
%>
<% if (selectedProduct != null) { %>
<ol class="page-breadcrumb breadcrumb__list">
    <li><a href="./home" class="breadcrumb__item">Trang chủ</a></li>
    <li><a href="" class="breadcrumb__item">/
        <%=selectedProduct.getId() %>
    </a></li>
</ol>

<div class="container p-3">
    <div class="row">
        <div class="col-md-6 p-5 border">
            <% if (imageMap != null) { %>
            <div class="image-carousel">
                <ul class="image-list">
                    <% for (Map.Entry<String, String> entry :
                            imageMap.entrySet()) { %>
                    <li>
                        <img src="<%=entry.getValue()%>"
                             alt=""
                             data-zoom-image="<%=entry.getValue()%>">
                    </li>
                    <% } %>
                </ul>
                <div class="image-display">
                    <% String firstImageUrl = "";
                        if
                        (!imageMap.isEmpty()) {
                            Map.Entry<String, String> firstEntry =
                                    imageMap.entrySet().iterator().next();
                            firstImageUrl = firstEntry.getValue();
                        }
                    %>
                    <img src="<%=firstImageUrl%>"
                         id="main-image" alt="Main Image">
                </div>
            </div>
            <% } else { %>
            <p>No images found for this product.</p>
            <% } %>
        </div>

        <div class="col-md-6 p-5 border bg-white">
            <h2>
                <%=selectedProduct.getName()%>
            </h2>
            <div class="d-flex flex-row my-3">
                <div class="text-warning mb-1 me-2">
                    <div class="rating-container">
                        <span class="rating-stars">
                            <% for (int i = 1; i <= Math.floor(productRating); i++) {
                                %>
                            <i class="fa fa-star text-primary"></i>
                            <% } %>
                            <% if (productRating % 1 > 0) {%>
                            <i class="fas fa-star-half-alt text-primary"></i>
                            <% } %>
                            <% for (int i = (int) (Math.ceil(productRating) + 1); i <= 5; i++) {%>
                            <i class="fa-regular fa-star"></i>
                            <% } %>
                        </span><span class="rating-value ms-1">
                        <%=productRating%>
                    </span>
                    </div>
                </div>
            </div>
            <p class="text-justify">Số lượng hàng: <%=
            selectedProduct.getQuantity() %>
            </p>
            <p class="price">Giá: <%= selectedProduct.getPrice() %>đ
            </p>

            <p class="text-justify">Mô tả sản phẩm:</p>
            <p class="product--description">Chất liệu:
                <%=selectedProduct.getMaterial()%>, Kích thước:
                <%=selectedProduct.getSize()%>
                , màu sắc: <%=selectedProduct.getColor()%>
            </p>

            <div class="order">
                <button class="btn btn-primary btn-lg" onclick="addToCart('<%= selectedProduct.getId() %>')">
                    <i class="fa-solid fa-shopping-cart"></i>
                    <span class="cart-text">Thêm vào giỏ hàng</span>
                </button>
            </div>
        </div>

        <%--Feedback Form--%>
        <form id="feedbackForm" action="./submitFeedback"
              method="post">
            <div class="mb-3">
                <label for="feedbackText" class="form-label"
                       style="font-size: 2.1rem;">Đánh giá sản phẩm:</label>
                <textarea class="form-control" id="feedbackText"
                          rows="5" cols="33"
                          name="content"></textarea>
            </div>
            <input type="hidden" id="productId" name="productId"
                   value="<%=selectedProduct.getId() %>">
            <input type="hidden" id="isLoggedIn"
                   value="<%=(account != null) ? " true" : "false"%>">

            <div class="button-container">
                <button type="submit"
                        class="btn btn-primary btn-lg"
                        id="submitButton">Gửi phản hồi
                </button>
            </div>
        </form>

        <div class="rating-container"
             style="position: relative; left: 212px; top: -195px; transition: none 0s ease 0s;">
            <form id="ratingForm" action="./rateProduct"
                  method="post">
                <input type="hidden" name="productId"
                       value="<%= selectedProduct.getId() %>">
                <input type="hidden" id="selectedRating"
                       name="selectedRating" value="">
                <div class="rating" id="starRating">
                    <i class="star" data-rating="1"
                       onmouseover="highlightStars(1)"
                       onmouseout="resetStars()">★</i>
                    <i class="star" data-rating="2"
                       onmouseover="highlightStars(2)"
                       onmouseout="resetStars()">★</i>
                    <i class="star" data-rating="3"
                       onmouseover="highlightStars(3)"
                       onmouseout="resetStars()">★</i>
                    <i class="star" data-rating="4"
                       onmouseover="highlightStars(4)"
                       onmouseout="resetStars()">★</i>
                    <i class="star" data-rating="5"
                       onmouseover="highlightStars(5)"
                       onmouseout="resetStars()">★</i>
                </div>
            </form>
        </div>

        <%-- Comments Section --%>
        <h3>Các lượt đánh giá sản phẩm (<%= (comments
                != null) ? comments.size() : 0 %>)</h3>
        <% if (comments != null && !comments.isEmpty()) {
            for
            (Comment comment : comments) { %>
        <div class="comment-item d-flex mb-3">
            <div class="profile-pic">
                <img src="./assets/images/facebook-user-icon-19.jpg"
                     alt="User Avatar">
            </div>

            <div class="comment-content flex-grow-1">
                <p class="comment-author">
                    <% int
                            accountId = comment.getIdAccount();
                        if (accountId > 0) {
                            Account commenterAccount =
                                    AccountService.getInstance().getAccountByAccountId(accountId);
                            if (commenterAccount != null) {
                    %>
                    <%= commenterAccount.getUsername()
                    %>
                    <% }
                    } else { %>
                    Anonymous User
                    <% } %>
                </p>
                <p class="comment-text">
                    <%= comment.getContent() %>
                </p>
                <% if (comment.getDateComment() != null) { %>
                <p class="comment-date">
                    <%=
                    comment.getDateComment().toString()%>
                </p>
                <% } %>
            </div>
        </div>
        <% }
        } else { %>
        <p>Chưa có đánh giá nào. Hãy là người đầu
            tiên để lại bình luận!</p>
        <% } %>
    </div>
        <% } %>
    <jsp:include page="footer.jsp"/>
</body>
<script src="https://use.fontawesome.com/releases/v6.4.2/js/all.js"
        crossorigin="anonymous"></script>
<script
        src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script
        src="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.9.0/slick.min.js"
        integrity="sha512-HGOnQO9+SP1V92SrtZfjqxxtLmVzqZpjFFekvzZVWoiASSQgSr4cw9Kqd2+l8Llp4Gm0G8GIFJ4ddwZilcdb8A=="
        crossorigin="anonymous" referrerpolicy="no-referrer"></script>

<!--    Slider-->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css"
      rel="stylesheet"
      integrity="sha384-EVSTQN3/azprG1Anm3QDgpJLIm9Nao0Yz1ztcQTwFspd3yD65VohhpuuCOmLASjC"
      crossorigin="anonymous">
<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-MrcW6ZMFYlzcLA8Nl+NtUVF0sA7MsXsP1UyJoMp4YLEuNSfAP+JcXn/tWtIaxVXM"
        crossorigin="anonymous"></script>

<script type="text/javascript">

    $(document).ready(function () {
        const imageCarousel = $('.image-carousel');
        const mainImage = $('#main-image');
        const imageDisplay = $('.image-display');

        imageCarousel.on('click', '.image-list li img', function (event) {
            const zoomImage = $(this).data('zoomImage');
            mainImage.attr('src', '');
            imageDisplay.addClass('active');
        });

        imageDisplay.on('click', function () {
            imageDisplay.removeClass('active');
            mainImage.attr('src', '');
        });
    });

    function highlightStars(count) {
        const stars = document.querySelectorAll('.star');
        stars.forEach((star, index) => {
            if (index < count) {
                star.style.color = 'gold';
            }
        });
    }

    function resetStars() {
        const stars = document.querySelectorAll('.star');
        stars.forEach(star => {
            star.style.color = 'black';
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        const stars = document.querySelectorAll('.star');
        const ratingInput = document.getElementById('selectedRating');

        stars.forEach(star => {
            star.addEventListener('click', function () {
                const rating = this.dataset.rating;
                ratingInput.value = rating;
                document.getElementById('ratingForm').submit();
            });
        });
    });

    document.addEventListener('DOMContentLoaded', function () {
        const stars = document.querySelectorAll('.star');
        const ratingInput = document.getElementById('selectedRating');

        stars.forEach(star => {
            star.addEventListener('click', function () {
                if (<%= session.getAttribute("account") != null %>) {
                    const rating = this.dataset.rating;
                    ratingInput.value = rating;
                    document.getElementById('ratingForm').submit();
                } else {
                    window.location.href = './login';
                }
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
</script>

</html>