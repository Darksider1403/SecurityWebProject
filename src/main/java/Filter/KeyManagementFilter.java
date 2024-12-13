package Filter;

import DAO.KeyDAO;
import Model.Account;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebFilter("/users-page.jsp")
public class KeyManagementFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();
        Account account = (Account) session.getAttribute("account");

        if (account != null) {
            // Get the user's keys
            List<Map<String, Object>> publicKeys = KeyDAO.getKeysByUserId(account.getID());
            boolean canGenerateKey = !KeyDAO.hasActiveKey(account.getID());

            // Set these as request attributes
            request.setAttribute("publicKeys", publicKeys);
            request.setAttribute("canGenerateKey", canGenerateKey);
        }

        // Continue with the request
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
