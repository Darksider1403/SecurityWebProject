package Controller;

import Model.ShoppingCart;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CartSizeServlet", value = "/CartSizeServlet")
public class CartSizeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        if (cart == null) cart = new ShoppingCart();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String json = new Gson().toJson(new Response(cart.getSize()));
        resp.getWriter().write(json);
    }

    private class Response {
        private int cartSize;

        public Response(int cartSize) {
            this.cartSize = cartSize;
        }

        public int getCartSize() {
            return cartSize;
        }
    }
}
