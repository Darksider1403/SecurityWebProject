package Controller;

import Model.Account;
import Model.Product;
import Service.ProductService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/productDetail")
public class ProductDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getParameter("id");
        System.out.println(productId);

        if (productId != null) {
//            List<Product> listProduct = ProductService.getInstance().findByCategory(0);
            HttpSession session = request.getSession(true);

            Account account = (Account) session.getAttribute("account");
            Product selectedProduct = ProductService.getInstance().findById(productId);
            ProductService productService = ProductService.getInstance();
            Double productRating = ProductService.getInstance().getRating(productId);

            System.out.println(productRating);

            response.setContentType("text/html;charset=UTF-8");

            if (account != null) {
                session.setAttribute("account", account);
            }

            RequestDispatcher rd = request.getRequestDispatcher("/productDetail.jsp");
            request.setAttribute("selectedProduct", selectedProduct);
            request.setAttribute("ps", productService);
            request.setAttribute("productRating", productRating);

            Map<String, String> listImagesThumbnail = ProductService.getInstance().selectImageProductDetail(productId);
            request.setAttribute("listImagesThumbnailForProductDetail", listImagesThumbnail);
            rd.forward(request, response);
        } else {
            System.out.println("error");
        }
    }
}
