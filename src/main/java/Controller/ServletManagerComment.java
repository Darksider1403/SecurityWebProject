package Controller;

import Model.Comment;
import Service.FeedbackAndRatingService;
import Service.PaginationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ServletManagerComment", value = "/managerComment")
public class ServletManagerComment extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        PaginationService ps = PaginationService.getInstance();
        FeedbackAndRatingService feedbackAndRatingService = FeedbackAndRatingService.getInstance();
        String id = req.getParameter("id");
        String pageCurrent = req.getParameter("page") == null ? "1" : req.getParameter("page");
        String search = req.getParameter("search");
        int page = Integer.parseInt(pageCurrent) - 1;
        int totalComment;

        System.out.println("id: " + id);
        List<Comment> commentList;

        if (search == null || search.isEmpty()) {
            commentList = ps.commentList();

            totalComment = feedbackAndRatingService.getTotalComment();
        } else {
            commentList = ps.findComment(search, 12, page * 12);

            totalComment = feedbackAndRatingService.totalCommentBySearch(search);
        }

        int totalPage = totalComment / 12;
        if (totalComment % 12 != 0) {
            totalPage++;
        }

        req.setAttribute("totalComment", totalComment);
        req.setAttribute("feedbackAndRatingService", feedbackAndRatingService);
        req.setAttribute("commentList", commentList);
        req.setAttribute("totalPage", totalPage);
        req.setAttribute("pageCurrent", pageCurrent);


            req.getRequestDispatcher("ManagerComment.jsp").forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
