
package Controller;

import Model.Account;
import Service.AccountService;
import Service.EncryptService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ServletRegister", value = "/register")
public class ServletRegister extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String fullname = req.getParameter("fullname") == null ? "" : req.getParameter("fullname");
        String email = req.getParameter("email") == null ? "" : req.getParameter("email");
        String phone = req.getParameter("phone") == null ? "" : req.getParameter("phone");
        String username = req.getParameter("username") == null ? "" : req.getParameter("username");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String repeatPassword = req.getParameter("repeatPassword") == null ? "" : req.getParameter("repeatPassword");
        String error = "";
        String notify = "";
        AccountService as = AccountService.getInstance();
        if (fullname.isEmpty() || email.isEmpty() || phone.isEmpty()
                || username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            error = "Vui lòng nhập đầy đủ các trường dữ liệu";
        } else {
            if (!as.isEmail(email)) {
                error = "Vui lòng nhập đúng định dạng email";
            } else if (!as.isPhoneValid(phone)) {
                error = "Vui lòng nhập đúng định dạng số điện thoại";
            } else if (!as.checkValidatePassword(password)) {
                error = "Password không đủ mạnh";
            } else if (username.contains(" ")) {
                error = "Username không được chứa khoảng cách";
            } else if (!password.equals(repeatPassword)) {
                error = "Password không trùng nhau";
            } else {
                if (as.accountByUsername(username) == null && as.accountByUsernameAndEmail(username, email) == null) {
                    EncryptService es = EncryptService.getInstance();
                    String encryptPass = es.encryptMd5(password);
                    if (as.createAccount(username,encryptPass,email, fullname, phone, 0) != 0) {
                        Account account = as.accountByUsername(username);
                        if (as.vertifyEmail(account)) {
                            error = "Vui lòng xác minh lại email";
                        } else {
                            notify = "Đăng ký thành công";
                            req.setAttribute("notify", notify);
                            req.getRequestDispatcher("Register.jsp").forward(req, resp);
                        }
                    }
                }
            }
        }

        req.setAttribute("error", error);
        req.setAttribute("fullname", fullname);
        req.setAttribute("phone", phone);
        req.setAttribute("username", username);
        req.getRequestDispatcher("Register.jsp").forward(req, resp);
    }
}
