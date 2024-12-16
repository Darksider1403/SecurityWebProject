package Controller;

import DAO.ProductDAO;
import Model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.List;

@WebServlet(name = "SearchController", value = "/search")
public class SearchController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
            String content = req.getParameter("content") == null ? "" : req.getParameter("content");
            if (!content.isEmpty()) {
                List<Product> productList = ProductDAO.findProductBySearch(content);
                req.setAttribute("content", content);
                resp.setContentType("text/html;charset=UTF-8");
                PrintWriter pw = resp.getWriter();
                NumberFormat nf = NumberFormat.getInstance();
                for (Product p : productList) {
                    String source = ProductDAO.imageThumbByIdProduct(p.getId());
                    pw.println("<li class=\"header__product-item\">\n" +
                            "<div class=\"header__product-infor\">\n" +
                            "<a href=\"productDetail?id="+p.getId()+"\">" + p.getName() + "</a>\n" +
                            "<span>Giá: "+nf.format(p.getPrice())+"</span>\n" +
                            "</div>\n" +
                            "<a href=\"\" class=\"header__product-img\"><img src=\" " + source +  "\" alt=\"\"></a>\n" +
                            "</li>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
