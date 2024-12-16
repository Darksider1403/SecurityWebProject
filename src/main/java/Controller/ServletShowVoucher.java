package Controller;

import Model.Voucher;
import Service.VoucherService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "ServletVoucherShow", value = "/createVoucher")
public class ServletShowVoucher extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Voucher> vouchers = VoucherService.getInstance().getVouchers();
        req.setAttribute("vouchers", vouchers);
        req.getRequestDispatcher("/voucher.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        Date dateStart = Date.valueOf(req.getParameter("dateStart"));
        Date dateEnd = Date.valueOf(req.getParameter("dateEnd"));
        double discount = Double.parseDouble(req.getParameter("discount"));

        int result = VoucherService.getInstance().createVoucher(name, dateStart, dateEnd, discount);

        if (result > 0) {
            req.setAttribute("notify", "Thêm voucher thành công!");
        } else {
            req.setAttribute("notify", "Thêm voucher không thành công!");
        }

        req.getRequestDispatcher("/voucher.jsp").forward(req, resp);
    }
}
