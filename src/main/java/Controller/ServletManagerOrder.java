package Controller;

import DAO.OrderDAO;
import Model.Order;
import Model.Order_detail;
import Service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ServletManagerOrder", value = "/managerOrder")
public class ServletManagerOrder extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        ProductService productService = ProductService.getInstance();
        String id = req.getParameter("id");
        List<Order> orderList;
        orderList = OrderDAO.getOrderList();
        if (id != null) {
            List<Order_detail> orderDetailList = OrderDAO.orderDetailList(id);
            int totalPrice = OrderDAO.totalPriceOrderDetail(id);
            Order order = OrderDAO.orderById(id);
            req.setAttribute("order", order);
            req.setAttribute("orderDetailList", orderDetailList);
            req.setAttribute("totalPrice", totalPrice);
        }
        req.setAttribute("id", id);
        req.setAttribute("ps", productService);
        req.setAttribute("orderList", orderList);
        req.getRequestDispatcher("ManagerOrder.jsp").forward(req, resp);
    }
}
