package Controller;

import Model.ShoppingCart;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "AddToCartServlet", value = "/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("AddToCartServlet doPost method is called");
        HttpSession session = req.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) cart = new ShoppingCart();
        String maSp = req.getParameter("masanpham");
        JSONObject jsonResponse = new JSONObject();

        if (maSp != null && !maSp.isEmpty()) {
            cart.add(maSp);
            session.setAttribute("cart", cart);
            jsonResponse.put("success", true);
            jsonResponse.put("cartSize", cart.getSize());
        } else {
            jsonResponse.put("success", false);
        }

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(jsonResponse.toString());
        out.flush();
    }
}
