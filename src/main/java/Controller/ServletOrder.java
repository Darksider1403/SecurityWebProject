package Controller;

import DAO.LogDAO;
import DAO.LogDAOImp;
import Model.*;
import Service.OrderService;
import Service.ProductService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ServletOrder", value = "/ServletOrder")
public class ServletOrder extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        ProductService ps = new ProductService();

        if (account != null) {
            int id = account.getID();
            String phoneNumber = req.getParameter("phoneNumber");
            String address = req.getParameter("addressInput");
            int status = 0;
            Date datebuy = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(datebuy);
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            Date arrivalDate = calendar.getTime();
            boolean addOrder = OrderService.addOrder(address, status, id, datebuy, arrivalDate, phoneNumber);

            Map<String, String> listImagesThumbnail = ps.selectImageThumbnail();
            req.getSession().setAttribute("listImagesThumbnail", listImagesThumbnail);

            if (addOrder) {
                List<CartItems> cartItemsList = (List<CartItems>) session.getAttribute("list-sp");
                for (CartItems cartItem : cartItemsList) {
                    int quantity = cartItem.getQuantity();
                    int price = cartItem.getProduct().getPrice();
                    String id_product = cartItem.getProduct().getId();
                    OrderService.getInstance().addProductToOrder(id_product, quantity, price);
                    OrderService.decrementQuantity(List.of(id_product), quantity);
                }

                clearCart(session);

                // Logging the order
                LogDAO logDAO = new LogDAOImp();
                Log log = new Log();
                log.setLevel(Log_Level.INFO);
                log.setIp(req.getRemoteAddr()); // Get client's IP address
                log.setAddress(address);
                log.setPreValue(""); // No previous value
                log.setValue("Order placed successfully");
                log.setCountry(""); // Set the country if available
                log.setStatus("SUCCESS"); // Log status
                logDAO.add(log);

                resp.sendRedirect("home");
            } else {
                resp.sendRedirect("error.jsp");
            }
        } else {
            resp.sendRedirect("index.jsp");
        }
    }

    private void clearCart(HttpSession session) {
        session.removeAttribute("cart");
    }
}