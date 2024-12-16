package Controller;

import DAO.KeyDAO;
import Model.KeyReport;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/managerKeyReports")
public class ServletManagerKeyReports extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<KeyReport> keyReports = new KeyDAO().getAllKeyReports();
        request.setAttribute("keyReports", keyReports);
        request.getRequestDispatcher("ManagerKeyReports.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int reportId = Integer.parseInt(request.getParameter("reportId"));
        int userId = Integer.parseInt(request.getParameter("userId"));

        KeyDAO.approveKeyReport(reportId, userId);
        response.sendRedirect("managerKeyReports");
    }
}
