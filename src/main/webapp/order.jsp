<%@ page import="java.text.NumberFormat" %>
<%@ page import="Model.CartItems" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="Model.Account" %>
<%@ page import="DAO.OrderDAO" %>
<%@ page import="Service.ProductService" %>
<%@ page import="Model.Voucher" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="Service.VoucherService" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="Service.GHNApiUtil" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Shopping Cart - Order Summary and Checkout</title>

    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <!-- Bootstrap Bundle with Popper -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style type="text/css">
        body {
            margin-top: 20px;
            background-color: #f1f3f7;
        }
        .avatar-lg {
            height: 5rem;
            width: 5rem;
        }
        .font-size-18 {
            font-size: 18px !important;
        }
        .text-truncate {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        a {
            text-decoration: none !important;
        }
        .w-xl {
            min-width: 160px;
        }
        .card {
            margin-bottom: 24px;
            box-shadow: 0 2px 3px #e4e8f0;
        }
        .card {
            display: flex;
            flex-direction: column;
            background-color: #fff;
            border: 1px solid #eff0f2;
            border-radius: 1rem;
        }
        .discount-detail {
            margin-bottom: 20px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        .discount-detail p {
            margin: 0;
            padding: 5px 0;
        }
        .discount-detail p strong {
            font-weight: bold;
            color: #333;
        }
        .address {
            display: flex;
            width: 480px;
            justify-content: space-between;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/MaterialDesign-Webfont/5.3.45/css/materialdesignicons.css" crossorigin="anonymous" />
    <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
</head>
<body>
<jsp:include page="header.jsp"></jsp:include>

<div class="container">

    <div class="row">
        <div class="col-xl-8">
            <%
                Account account = (Account) session.getAttribute("account");
                NumberFormat nf = NumberFormat.getInstance();
                Double discount = (Double) session.getAttribute("discount");
                List<CartItems> sanPhams = (List<CartItems>) session.getAttribute("list-sp");
                double tongGiaTri = 0;
                int fee = request.getAttribute("fee") == null ? 0 : (int)request.getAttribute("fee");
                Map<String, String> listImagesThumbnail = ProductService.getInstance().selectImageThumbnail();
                for (CartItems sp : sanPhams) {
                    tongGiaTri += sp.getTotalPrice();
            %>
            <div class="card border shadow-none">
                <div class="card-body">
                    <div class="d-flex align-items-start border-bottom pb-3">
                        <div class="me-4">
                            <%
                                String productId = sp.getProduct().getId();
                                String imageSource = listImagesThumbnail.get(productId);
                            %>
                            <img src="<%= imageSource %>" alt class="avatar-lg rounded">
                        </div>
                        <div class="flex-grow-1 align-self-center overflow-hidden">
                            <div>
                                <h5 class="text-truncate font-size-18">
                                    <a href="#" class="text-dark"><%= sp.getProduct().getName() %></a>
                                </h5>
                                <p class="mb-0 mt-1">ID : <span class="fw-medium"><%= sp.getProduct().getId() %></span></p>
                            </div>
                        </div>
                    </div>
                    <div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="mt-3">
                                    <p class="text-muted mb-2">Giá</p>
                                    <h5 class="mb-0 mt-2">
                                <span class="text-muted me-2">
                                   <p class="font-size-16 "><%= nf.format(sp.getProduct().getPrice()) %>đ</p>
                                </span>
                                    </h5>
                                </div>
                            </div>
                            <div class="col-md-5">
                                <div class="mt-3">
                                    <p class="text-muted mb-2">Số lượng</p>
                                    <h5><%= sp.getQuantity() %></h5>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="mt-3">
                                    <p class="text-muted mb-2">Total</p>
                                    <h5><%= nf.format(sp.getProduct().getPrice() * sp.getQuantity()) %>
                                    </h5>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <% } // End of for loop %>
            <form action="ServletOrder" method="post">
                <div class="text-warning mb-3">
                    <span class="input-group-text" id="basic-addon2">Xin lưu ý nhập đúng những thông tin bên dưới để chúng tôi có thể giao hàng chính xác</span>
                </div>
                <div class="input-group mb-3">
                    <span class="input-group-text" id="basic-addon1">Số Điện Thoại</span>
                    <input type="text" name="phoneNumber" required class="form-control" id="phoneNumber"
                           placeholder="Nhập số điện thoại" aria-label="Số điện thoại" aria-describedby="basic-addon1"
                           pattern="[0-9]{1,10}">
                </div>
                <div class="address">
                    <div id="selectAddress">
                        <select id="provinceSelect" onchange="changeProvince()" name="province">
                            <option value="">Tỉnh/thành phố</option>
                            <%
                                JSONArray provinces = (JSONArray) request.getAttribute("provinces");
                                for (int i = 0; i < provinces.length(); i++) {
                                    JSONObject province = provinces.getJSONObject(i);
                                    String provinceName = province.getString("ProvinceName");
                                    int ProvinceID = province.getInt("ProvinceID");%>
                            <option value="<%=ProvinceID%>"><%=provinceName%></option>
                            <%}%>
                            %>
                        </select>
                    </div>
                    <div id="districtContainer">
                    </div>
                    <div id="wardContainer"></div>
                </div>
                <div class="row my-4">
                    <div class="col-sm-6">
                        <a href="home.jsp" class="btn btn-link text-muted">
                            <i class="mdi mdi-arrow-left me-1"></i> Tiếp tục mua hàng
                        </a>
                    </div>
                    <div class="col-sm-6">
                        <button type="submit" class="btn btn-success" id="checkoutButton">
                            <i class="mdi mdi-cart-outline me-1"></i> Thanh toán
                        </button>
                    </div>
                </div>
            </form>
        </div>
        <div class="col-xl-4">
            <div class="mt-5 mt-lg-0 total-price">
                <div class="card border shadow-none">
                    <div class="card-header bg-transparent border-bottom py-3 px-4">
                        <h5 class="font-size-16 mb-0">Mã đơn hàng <span class="float-end"><%= OrderDAO.orderId() %></span></h5>
                    </div>
                    <div class="card-body p-4 pt-2">
                        <div class="table-responsive">
                            <table class="table mb-0">
                                <tbody>
                                <tr>
                                    <td>Tổng giá trị đơn hàng :</td>
                                    <td class="text-end">
                                        <span class="fw-bold"><%= nf.format(tongGiaTri) %>đ</span></td>
                                </tr>
                                <tr>
                                    <td>Chi phí vận chuyển :</td>
                                    <td class="text-end">
                                        <span id="fee" class="fw-bold"><%= nf.format(fee) %>đ</span></td>
                                </tr>
                                <tr class="bg-light">
                                    <th>Thanh toán :</th>
                                    <td class="text-end">
        <span id="thanhtoan" class="fw-bold">
            <%= nf.format(tongGiaTri) %>
        </span><td/>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <button type="button" class="btn btn-primary view-details-btn" data-toggle="modal" data-target="#voucherModal">
                    Xem Voucher
                </button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="voucherModal" tabindex="-1" role="dialog" aria-labelledby="voucherModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="voucherModalLabel">Voucher List</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <form id="voucherForm" action="./voucher" method="post">
                        <%
                        List<Voucher> voucherList = (List<Voucher>) VoucherService.getInstance().getVouchers();
                        if (voucherList == null || voucherList.isEmpty()) {
                            System.out.println("Null");
                    %>
                    <p>No vouchers available</p>
                        <%
                    } else {
                        for (Voucher voucher : voucherList) {
                    %>
                    <div class="voucher-item">
                        <input type="radio" name="selectedVoucher" value="<%= voucher.getId() %>">
                        <span class="voucherName"><%= voucher.getName() %></span>
                        <p>Start Date: <%= voucher.getDateStart() %></p>
                        <p>End Date: <%= voucher.getDateEnd() %></p>
                        <p>Discount: <%= voucher.getDiscount() %></p>
                    </div>
                        <%
                            }
                        }
                    %>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" onclick="applyVoucher()">Apply Voucher</button>
            </div>
            </form>
        </div>
    </div>
</div>
<script>

    $(document).ready(function(){
        $('.view-details-btn').click(function(){
            $('#voucherModal').modal('show');
        });
    });

    function changeProvince() {
        var ProvinceID = document.getElementById("provinceSelect").value;
        $.ajax({
            url: "order",
            type: "POST",
            data: {
                ProvinceID: ProvinceID
            },
            success: function (response) {
                $("#districtContainer").html(response);
                $("#wardContainer").html('<select id="wardSelect" name="ward"><option>Xã</option></select>');
            }
        })
    }

    function changeDistrict() {
        var DistrictID = document.getElementById("districtSelect").value;
        $.ajax({
            url: "order",
            type: "POST",
            data: {
                DistrictID: DistrictID
            },
            success: function (response) {
                $("#wardContainer").html(response);
            }
        })
    }


    function checkSelection() {
        var ProvinceID = document.getElementById("provinceSelect").value;
        var DistrictID = document.getElementById("districtSelect").value;
        var WardId = document.getElementById("wardSelect").value;

        if (!isNaN(ProvinceID) && !isNaN(DistrictID) && !isNaN(WardId)) {;
            $.ajax({
                url: "fee",
                type: "POST",
                data: {
                    ProvinceID: ProvinceID,
                    DistrictID: DistrictID,
                    WardId: WardId
                },
                success: function (response) {
                    var fees = document.getElementById("fee");
                    var fee = parseInt(response.fee);
                    fees.innerHTML = fee + "đ";
                    var grandTotal = (<%=tongGiaTri%> + fee) * <%=discount%>;
                    <% if(discount != null && discount > 0) {%>
                    grandTotal = (<%=tongGiaTri%> + fee) * <%=discount%>;

                    <%} else {%>
                    grandTotal = <%=tongGiaTri%> + fee;
                    <%}%>

                    var thanhtoan = document.getElementById("thanhtoan");
                    thanhtoan.innerHTML = grandTotal + "đ";
                }
            })
        }
    }
</script>
<jsp:include page="footer.jsp"/>
</body>
</html>
