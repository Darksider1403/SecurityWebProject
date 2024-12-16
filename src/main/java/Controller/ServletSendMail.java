package Controller;

import Model.Account;
import Service.AccountService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ServletSendMail", value = "/ServletSendMail")
public class ServletSendMail extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = ((Account) session.getAttribute("account")).getUsername();
        String email = ((Account) session.getAttribute("account")).getEmail();
        AccountService as = AccountService.getInstance();
        Account account = as.accountByUsernameAndEmail(username, email);
        boolean check = false;
        if (account != null) {
            if (as.forgotPassword(account)) {
                check = true;
                req.setAttribute("check", check);
                req.getRequestDispatcher("users-page.jsp").forward(req, resp);
            }
        }
    }
}
