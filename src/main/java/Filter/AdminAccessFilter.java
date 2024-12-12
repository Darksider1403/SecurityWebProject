package Filter;


import Model.Account;
import Service.AccountService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/admin", "/managerAccount", "/managerProduct", "/managerOrder", "/managerComment", "/createVoucher", "/managerLog"})
public class AdminAccessFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String url = httpRequest.getServletPath();

        HttpSession session = httpRequest.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account.getRole() == 0) {
            int role;
            AccountService accountService = AccountService.getInstance();
            role = accountService.getRoleByAccountId(account.getID());
            account.setRole(role);
        }

        if (account.getRole() == 1) {
            if (url.endsWith("/admin")
                    || url.endsWith("/managerAccount")
                    || url.endsWith("/managerProduct")
                    || url.endsWith("/managerOrder")
                    || url.endsWith("/createVoucher")
                    || url.endsWith("/managerLog")
                    || url.endsWith("/managerComment")) {
                httpResponse.sendRedirect("./home");
                return;
            }
        } else if (account.getRole() == 2 || account.getRole() == 3) {
            if (url.endsWith("/managerAccount")
                    || url.endsWith("/managerComment")) {
                httpResponse.sendRedirect("./admin");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}