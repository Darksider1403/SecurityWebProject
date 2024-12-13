package Controller;

import DAO.KeyDAO;
import Model.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/ServletReportKey")
public class ServletReportKey extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the current logged-in user
        Account account = (Account) request.getSession().getAttribute("account");
        if (account == null) {
            // If no user is logged in, redirect to login page
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Get the user's keys from the database
            List<Map<String, Object>> publicKeys = KeyDAO.getKeysByUserId(account.getID());

            // Check if the user can generate a new key
            boolean canGenerateKey = !KeyDAO.hasActiveKey(account.getID());

            request.setAttribute("publicKeys", publicKeys);
            request.setAttribute("canGenerateKey", canGenerateKey);

            request.getRequestDispatcher("users-page.jsp#key-management").forward(request, response);

        } catch (Exception e) {
            // Log the error and show an error message to the user
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while loading key information");
            request.getRequestDispatcher("users-page.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account account = (Account) request.getSession().getAttribute("account");
        if (account != null) {
            // Create the key report
            KeyDAO.createKeyReport(account.getID());

            // Redirect instead of forward
            response.sendRedirect("users-page.jsp#key-management");
        } else {
            response.sendRedirect("login.jsp");
        }
    }
}