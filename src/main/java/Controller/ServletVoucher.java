package Controller;

import Model.Voucher;
import Service.VoucherService;
import com.google.gson.Gson;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ServletVoucher", value = "/voucher")
public class ServletVoucher extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String selectedVoucherId = String.valueOf(Integer.parseInt(req.getParameter("selectedVoucher")));
        HttpSession session = req.getSession();
        System.out.println("here");

        // Apply voucher discount if a voucher is selected
        if (selectedVoucherId != null && !selectedVoucherId.isEmpty()) {
            Voucher voucher = VoucherService.getInstance().getVoucherByID(Integer.parseInt(selectedVoucherId));
            if (voucher != null) {
                // Apply discount to the order total
                double discount = voucher.getDiscount();

                // Update session attributes with discount details
                session.setAttribute("discount", discount);
            }
        }

        // Redirect back to the order page
        resp.sendRedirect("order");
    }
}
