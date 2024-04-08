package Controller;

import Model.CartItems;
import Model.ShoppingCart;
import Service.ProductService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CartServlet", value = "/CartServlet")
public class CartServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ShoppingCart gioHang = (ShoppingCart) req.getSession().getAttribute("cart");
        ProductService ps = new ProductService();
        if (gioHang == null) {
            gioHang = new ShoppingCart();
            req.getSession().setAttribute("cart", gioHang);
        }
        Map<String, String> listImagesThumbnail = ps.selectImageThumbnail();
        List<CartItems> danhSachSanPham = gioHang.getDanhSachSanPham();
        req.getSession().setAttribute("list-sp", danhSachSanPham);
        req.setAttribute("listImagesThumbnail", listImagesThumbnail);
        req.getRequestDispatcher("cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            calculateGrandTotal(request);
            // Other post handling logic
        }

        private void calculateGrandTotal(HttpServletRequest request) {
            double grandTotal = 0;
            List<CartItems> sanPhams = (List<CartItems>) request.getSession().getAttribute("list-sp");

            for (CartItems sp : sanPhams) {
                String checkboxParam = "isChecked_" + sp.getProduct().getId();
                String checkboxValue = request.getParameter(checkboxParam);
                if (checkboxValue != null && checkboxValue.equals("checked")) {
                    grandTotal += sp.getTotalPrice();
                }
            }

            request.getSession().setAttribute("grandTotal", grandTotal);
        }
    }

