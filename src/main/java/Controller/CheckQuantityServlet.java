package Controller;

import Model.Account;
import Model.CartItems;
import Model.ShoppingCart;
import Model.Product;
import Service.ProductService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@WebServlet(name = "CheckQuantityServlet", value = "/CheckQuantityServlet")
public class CheckQuantityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        ProductService productService = ProductService.getInstance();
        List<CartItems> cartItems = cart.getDanhSachSanPham();
        boolean allProductsAvailable = true;
        List<String> removedProductIds = new ArrayList<>(); // Danh sách lưu trữ các ID của sản phẩm bị loại bỏ

        Iterator<CartItems> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItems cartItem = iterator.next();
            String productId = cartItem.getProduct().getId();
            int requiredQuantity = cartItem.getQuantity();
            Product product = productService.findById(productId);

            if (product.getQuantity() < requiredQuantity) {
                iterator.remove();
                cart.remove(productId); // Loại bỏ sản phẩm khỏi giỏ hàng
                removedProductIds.add(productId); // Thêm ID của sản phẩm bị loại bỏ vào danh sách
                allProductsAvailable = false;//Biến dùng để check xem có sự thay đổi không
            }
        }

        session.setAttribute("cart", cart); // Cập nhật giỏ hàng trong session

        if (!allProductsAvailable) {
            // Chuyển hướng đến CartServlet với thông báo lỗi bao gồm các ID của sản phẩm bị xóa
            String errorMessage = "Các sản phẩm sau đây không đủ hàng và đã bị loại khỏi giỏ hàng: ";
            for (String removedProductId : removedProductIds) {
                errorMessage += removedProductId + ", ";
            }
            errorMessage = errorMessage.substring(0, errorMessage.length() - 2); // Xóa dấu phẩy cuối cùng và khoảng trắng
            resp.sendRedirect("CartServlet?errorMessage=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
            return;
        }
        if (account == null || cartItems.isEmpty()) {
            String message;
            if (account == null) {
                message = "Vui lòng đăng nhập để tiếp tục.";
                resp.sendRedirect("CartServlet?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));
                return;
            } else {
                message = "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm vào giỏ hàng trước khi tiếp tục.";
            }
            resp.sendRedirect("CartServlet?message=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));
            return;
        }

        resp.sendRedirect("order");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}


