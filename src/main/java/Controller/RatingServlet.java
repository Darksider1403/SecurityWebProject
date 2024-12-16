package Controller;

import Model.Account;
import Service.AccountService;
import Service.FeedbackAndRatingService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/rateProduct")
public class RatingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Account account = (Account) session.getAttribute("account");
        int idAccount = account.getID();
        String username = account.getUsername();

        if (username == null && idAccount == 0) {
            username = account.getName().trim().replace(" ", "");
            AccountService accountService = AccountService.getInstance();
            Account foundAccount = accountService.accountByUsername(username);
            if (foundAccount != null) {
                idAccount = foundAccount.getID();
            }
        }

        String selectedRatingString = request.getParameter("selectedRating");
        int rating;

        if (selectedRatingString != null && !selectedRatingString.isEmpty()) {
            try {
                int selectedRating = Integer.parseInt(selectedRatingString);
                rating = selectedRating;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect("./productDetail?id=" + request.getParameter("productId") + "&error=ratingFormat");
                return;
            }
        } else {
            response.sendRedirect("./productDetail?id=" + request.getParameter("productId") + "&error=noRating");
            return;
        }

        String productId = request.getParameter("productId");
        FeedbackAndRatingService feedbackService = FeedbackAndRatingService.getInstance();
        int rowsAffected = feedbackService.saveRating(rating, idAccount, productId);

        if (rowsAffected > 0) {
            response.sendRedirect("./productDetail?id=" + productId);
        } else {
            response.sendRedirect("./productDetail?id=" + productId + "&error=ratingSave");
        }
    }
}
