package DAO;


import Model.Comment;
import Service.AccountService;
import org.jdbi.v3.core.Jdbi;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;


public class FeedbackDAO {
    private static Jdbi JDBI;

    public static int saveCommentFeedback(String content, String productId, int idAccount) {
        String SAVE_FEEDBACK_SQL = "INSERT INTO reviews (content, dateComment, idProduct, idAccount) VALUES (?, ?, ?, ?)";
        String contentUTF8 = new String(content.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        LocalDate currentDate = LocalDate.now();
        JDBI = ConnectJDBI.connector();

        int execute = JDBI.withHandle(handle ->
                handle.createUpdate(SAVE_FEEDBACK_SQL)
                        .bind(0, contentUTF8)
                        .bind(1, currentDate)
                        .bind(2, productId)
                        .bind(3, idAccount)
                        .execute()
        );

        return execute;
    }

    public static List<Comment> getCommentsByProductId(String productId) {
        String GET_COMMENTS_SQL = "SELECT content, dateComment, idAccount FROM reviews WHERE idProduct = ?";
        JDBI = ConnectJDBI.connector();
        List<Comment> comments = JDBI.withHandle(handle ->
                handle.createQuery(GET_COMMENTS_SQL)
                        .bind(0, productId)
                        .mapToBean(Comment.class).stream().toList()
        );

        AccountService as = AccountService.getInstance();
        for (Comment comment : comments) {
            comment.setAccount(as.getAccountByAccountId(comment.getIdAccount()));
        }
        System.out.println("Number of comments retrieved: " + comments.size());

        return comments;
    }

    public static int getTotalNumberOfComments() {
        String GET_TOTAL_COMMENTS_SQL = "SELECT COUNT(*) as total FROM reviews";

        JDBI = ConnectJDBI.connector();
        int totalComments = JDBI.withHandle(handle ->
                handle.createQuery(GET_TOTAL_COMMENTS_SQL)
                        .mapTo(Integer.class)
                        .one()
        );

        return totalComments;
    }

    public static List<Comment> getCommentsByAccountId(int idAccount) {
        String GET_COMMENTS_SQL = "SELECT content, dateComment, idProduct FROM reviews WHERE idAccount = ?";
        JDBI = ConnectJDBI.connector();
        List<Comment> comments = JDBI.withHandle(handle ->
                handle.createQuery(GET_COMMENTS_SQL)
                        .bind(0, idAccount)
                        .mapToBean(Comment.class).stream().toList()
        );

        return comments;
    }

    public static int getTotalComment() {
        String GET_TOTAL_COMMENTS_SQL = "SELECT COUNT(*) as total FROM reviews";

        JDBI = ConnectJDBI.connector();
        int totalComments = JDBI.withHandle(handle ->
                handle.createQuery(GET_TOTAL_COMMENTS_SQL)
                        .mapTo(Integer.class)
                        .one()
        );

        return totalComments;
    }

    public static int totalCommentBySearch(String search) {
        JDBI = ConnectJDBI.connector();
        String sql = "SELECT COUNT(review.id) FROM reviews " +
                "JOIN products ON reviews.idProduct = products.id " +
                "JOIN accounts ON reviews.idAccount = accounts.id " +
                "WHERE reviews.content LIKE ?";
        int total = JDBI.withHandle(handle ->
                handle.createQuery(sql)
                        .bind(0, "%" + search + "%")
                        .mapTo(Integer.class)
                        .one()
        );

        return total;
    }

    public static int getStatusComment(int idAccount) {
        String GET_STATUS_COMMENT_SQL = "SELECT status FROM reviews WHERE idAccount = ?";
        JDBI = ConnectJDBI.connector();
        int status = JDBI.withHandle(handle ->
                handle.createQuery(GET_STATUS_COMMENT_SQL)
                        .bind(0, idAccount)
                        .mapTo(Integer.class)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No status found"))
        );

        return status;
    }

    public static List<String> getProductId(int idAccount) {
        String GET_PRODUCT_ID_SQL = "SELECT idProduct FROM reviews WHERE idAccount = ?";
        JDBI = ConnectJDBI.connector();
        List<String> productId = JDBI.withHandle(handle ->
                handle.createQuery(GET_PRODUCT_ID_SQL)
                        .bind(0, idAccount)
                        .mapTo(String.class)
                        .stream()
                        .toList()
        );

        return productId;
    }

    public static void main(String[] args) {
        System.out.println(getProductId(19));
    }
}


