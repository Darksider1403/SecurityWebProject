package Controller;

import DAO.AccountDAO;
import DAO.LogDAO;
import DAO.LogDAOImp;
import Model.Account;
import Model.Log;
import Model.Log_Level;
import Service.AccountService;
import Service.EncryptService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "ServletLogin", value = "/login")
public class ServletLogin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String username = req.getParameter("username") == null ? "" : req.getParameter("username");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        AccountService as = AccountService.getInstance();
        String hashPass = EncryptService.getInstance().encryptMd5(password);
        Account account = as.checkLogin(username, hashPass);

        // Logging the login attempt
        LogDAO logDAO = new LogDAOImp();
        Log log = new Log();

        // Get client's IP address
        String ipAddress = req.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = req.getRemoteAddr();
        }

        log.setIp(ipAddress);
        log.setAddress(username);
        log.setPreValue(""); // No previous value
        log.setCountry(""); // Set the country if available
        log.setDate(new Date(System.currentTimeMillis())); // Set the current timestamp

        if (username.isEmpty() || password.isEmpty()) {
            req.getRequestDispatcher("Login.jsp").forward(req, resp);
            // Log unsuccessful login attempt due to missing credentials
            logDAO.add(log);
        } else {
            if (account != null) {
                if (as.isLoginSuccess(account)) {
                    HttpSession session = req.getSession();
                    session.setAttribute("account", account);

                    // Log successful login
                    log.setLevel(Log_Level.INFO);
                    log.setValue("Login successful");
                    log.setStatus("SUCCESS");
                    logDAO.add(log);

                    if (AccountDAO.roleAccount(account.getID() + "") == 1) {
                        resp.sendRedirect("./home");
                    } else {
                        resp.sendRedirect("./admin");
                    }
                } else {
                    req.setAttribute("error", "Tài khoản chưa được xác nhận hoặc đã bị khóa");
                    req.getRequestDispatcher("Login.jsp").forward(req, resp);

                    // Log unsuccessful login attempt due to unconfirmed or locked account
                    log.setLevel(Log_Level.WARNING);
                    log.setValue("Login attempt failed: account not confirmed or locked");
                    log.setStatus("FAILED");
                    logDAO.add(log);
                }
            } else {
                req.setAttribute("error", "Bạn nhập sai email hoặc mật khẩu");
                req.getRequestDispatcher("Login.jsp").forward(req, resp);

                // Log unsuccessful login attempt due to wrong credentials
                log.setLevel(Log_Level.WARNING);
                log.setValue("Login attempt failed: wrong username or password");
                log.setStatus("FAILED");
                logDAO.add(log);
            }
        }
    }
}
