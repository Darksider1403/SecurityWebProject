package Controller;

import Model.Log;
import Service.LogService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogServlet", value = "/managerLog")
public class LogServlet extends HttpServlet {
    private LogService logService = LogService.getInstance();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List<Log> logs = logService.getAllLogs(page);
        request.setAttribute("logs", logs);

        request.getRequestDispatcher("/ManagerLog.jsp").forward(request, response);
    }
}
