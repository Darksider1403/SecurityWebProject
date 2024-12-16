package Controller;

import Model.Account;
import Model.Comment;
import Service.AccountService;
import Service.FeedbackAndRatingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@WebServlet("/submitFeedback")
public class FeedbackServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session != null) {
            Account account = (Account) session.getAttribute("account");

            if (account != null) {
                String content = request.getParameter("content");
                String productId = request.getParameter("productId");
                int idAccount = account.getID();
                String username = account.getUsername();

                if (username == null && idAccount == 0) {
                    username = account.getName().trim().replace(" ", "");
                    AccountService accountService = AccountService.getInstance();
                    Account foundAccount = accountService.accountByUsername(username);
                    if (foundAccount != null) {
                        idAccount = foundAccount.getID();
                        String name = foundAccount.getName();
                        name = account.getName();
                    }
                }

                FeedbackAndRatingService feedbackService = FeedbackAndRatingService.getInstance();
                int updateCount = feedbackService.saveCommentFeedback(content, productId, idAccount);
                if (updateCount > 0) {
                    List<Comment> comments = feedbackService.getCommentsByProductId(productId);

                    for (Comment comment : comments) {
                        Account commenterAccount = AccountService.getInstance()
                                .getAccountByAccountId(comment.getAccount().getID());
                        comment.setAccount(commenterAccount);
                    }

                    request.setAttribute("comments", comments);
                    request.setAttribute("username", username);

                    response.sendRedirect(request.getContextPath() +
                            "./productDetail?id=" + productId + "&feedbackError=true");
                } else {
                    response.sendRedirect("./productDetail?id=" + productId + "&feedbackError=false");
                }
            } else {
                response.sendRedirect("./login");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/errorPage");
        }
    }
}


