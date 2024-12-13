package Controller;


import Constants.Constants;
import Model.Account;
import Service.AccountService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(name = "LoginGoogleHandler", value = "/LoginGoogleHandler")
public class LoginGoogleHandler extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String code = request.getParameter("code");
        HttpSession session = request.getSession(true);
        Account account = (Account) session.getAttribute("account");
        String accessToken = getToken(code);
        Account user = getUserInfo(accessToken);
        AccountService as = AccountService.getInstance();
        int defaultRole = 1;
        String name = user.getName();
        String nameWithoutSpace = name.trim().replace(" ", "");
        int defaultStatus = 1;

        if (as.isAccountExist(user.getEmail())) {
            as.createAccountWithGoogleAndFacebook(nameWithoutSpace, user.getEmail(), user.getName(), defaultStatus);

            setIdAndUsername(user);

            as.createRoleAccount(user, defaultRole);
            logActivity("User " + user.getID() + " Create account");
            response.sendRedirect("/home");
        } else {
            setIdAndUsername(user);
            if (user.getRole() == 0) {
                defaultRole = as.getRoleByAccountId(user.getID());
                user.setRole(defaultRole);
            }

            if (user.getRole() == 1) {
                response.sendRedirect("/home");
            } else {
                response.sendRedirect("/admin");
            }
            logActivity("User " + user.getID() + " Already has an account");
        }
        System.out.println(user);
        session.setAttribute("account", user);
    }

    private void setIdAndUsername(Account user) {
        int idAccount = user.getID();
        String username = user.getUsername();
        if (username == null && idAccount == 0) {
            username = user.getName().trim().replace(" ", "");
            AccountService accountService = AccountService.getInstance();
            Account foundAccount = accountService.accountByUsername(username);
            if (foundAccount != null) {
                idAccount = foundAccount.getID();
                user.setUsername(username);
                user.setID(idAccount);
            }
        }
    }

    private void logActivity(String message) {
        System.out.println("Activity log: " + message);
    }

    public static String getToken(String code) throws ClientProtocolException, IOException {
        // call api to get token
        String response = Request.Post(Constants.GOOGLE_LINK_GET_TOKEN)
                .bodyForm(Form.form().add("client_id", Constants.GOOGLE_CLIENT_ID)
                        .add("client_secret", Constants.GOOGLE_CLIENT_SECRET)
                        .add("redirect_uri", Constants.GOOGLE_REDIRECT_URI).add("code", code)
                        .add("grant_type", Constants.GOOGLE_GRANT_TYPE).build())
                .execute().returnContent().asString();

        JsonObject jobj = new Gson().fromJson(response, JsonObject.class);
        String accessToken = jobj.get("access_token").toString().replaceAll("\"", "");
        return accessToken;
    }

    public static Account getUserInfo(final String accessToken) throws ClientProtocolException, IOException {
        String link = Constants.GOOGLE_LINK_GET_USER_INFO + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        Account googlePojo = new Gson().fromJson(response, Account.class);

        return googlePojo;
    }
}