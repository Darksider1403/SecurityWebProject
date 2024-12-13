package Controller;

import DAO.KeyDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ServletApproveKeyReport")
public class ServletApproveKeyReport extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int reportId = Integer.parseInt(request.getParameter("reportId"));
        int userId = Integer.parseInt(request.getParameter("userId"));

        // Call KeyDAO to approve the report
        KeyDAO.approveKeyReport(reportId, userId);

        // Redirect back to key reports page
        response.sendRedirect("managerKeyReports");
    }
}
