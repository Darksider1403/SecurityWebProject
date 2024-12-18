package Controller;

import DAO.OrderDAO;
import Service.LogService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "ServletReportOrder", value = "/ServletReportOrder")

public class ServletReportOrder extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        System.out.println(" here ưhy");

        String reportReason = request.getParameter("reportReason");

        try {
            // Update order status to 4 (being reprocessed)
            OrderDAO orderDAO = new OrderDAO();
            boolean updated = orderDAO.updateOrderStatus(orderId, 4);

            if (updated) {
                // Log the report reason (optional)
//                LogService.getInstance().logOrderReport(orderId, reportReason);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Đơn hàng đã được báo cáo thành công.");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Không thể cập nhật trạng thái đơn hàng.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Đã xảy ra lỗi: " + e.getMessage());
        }
    }
}
