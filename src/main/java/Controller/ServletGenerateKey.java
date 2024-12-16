package Controller;

import DAO.KeyDAO;
import Model.Account;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@WebServlet("/ServletGenerateKey")
public class ServletGenerateKey extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account account = (Account) request.getSession().getAttribute("account");
        List<Map<String, Object>> keys = KeyDAO.getKeysByUserId(account.getID());
        request.setAttribute("publicKeys", keys);
        request.setAttribute("canGenerateKey", !KeyDAO.hasActiveKey(account.getID()));
        request.getRequestDispatcher("Users").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Account account = (Account) request.getSession().getAttribute("account");

//            if (KeyDAO.hasActiveKey(account.getID())) {
//                response.sendRedirect("users-page.jsp?error=active_key_exists");
//                return;
//            }

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

            KeyDAO.saveKey(account.getID(), publicKey, true);

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"private_key.txt\"");
            try (PrintWriter writer = response.getWriter()) {
                writer.write(privateKey);
            }
            response.sendRedirect("users-page.jsp");
        } catch (Exception e) {
            throw new ServletException("Error generating keys", e);
        }
    }
}
