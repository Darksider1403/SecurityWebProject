package Controller;
import DAO.ConnectJDBI;
import DAO.OrderDAO;
import org.jdbi.v3.core.Jdbi;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ServletSignatureVerify", value = "/ServletSignatureVerify")
public class ServletSignatureVerify extends HttpServlet {
    private static Jdbi JDBI;
    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderIdParam = request.getParameter("orderId");
        System.out.println("Received orderIdParam: " + orderIdParam);

        if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing or invalid orderId parameter.");
            return;
        }

        try {
            if (!checkOrderDetailsExist(orderIdParam)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("No order details found for order: " + orderIdParam);
                return;
            }

            boolean isVerified = verifyOrderSignature(orderIdParam);

            if (isVerified) {
                System.out.println("Signature for order " + orderIdParam + " is valid.");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Signature is valid for order: " + orderIdParam);
            } else {
                System.out.println("Signature for order " + orderIdParam + " verification failed.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Signature verification failed for order: " + orderIdParam);
            }
        } catch (Exception e) {
            System.err.println("Error processing verification request: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred during verification: " + e.getMessage());
        }
    }

    private boolean checkOrderDetailsExist(String orderId) {
        System.out.println("Checking order details for: " + orderId);
        JDBI = ConnectJDBI.connector();
        String checkSql = "SELECT COUNT(*) as count FROM order_details WHERE idOrder = ?";

        Integer count = JDBI.withHandle(handle ->
                handle.createQuery(checkSql)
                        .bind(0, orderId)
                        .mapTo(Integer.class)
                        .one()
        );

        System.out.println("Found " + count + " order details");
        return count > 0;
    }

    public boolean verifyOrderSignature(String orderId) {
        try {
            JDBI = ConnectJDBI.connector();
            String sql = "SELECT s.Signature as signature, " +
                    "u.public_key, " +
                    "COALESCE(GROUP_CONCAT(DISTINCT CONCAT(od.idProduct, od.quantity, ROUND(od.price, 0))), '') AS orderData, " +
                    "o.address, " +
                    "o.numberPhone " +
                    "FROM signature s " +
                    "JOIN user_keys u ON s.id_key = u.id " +
                    "JOIN order_details od ON s.id_order = od.idOrder " +
                    "JOIN orders o ON o.id = s.id_order " +
                    "WHERE s.id_order = ? " +
                    "GROUP BY s.id_order, s.Signature, u.public_key, o.address, o.numberPhone";

            return JDBI.withHandle(handle -> {
                return handle.createQuery(sql)
                        .bind(0, orderId)
                        .mapToMap()
                        .findFirst()
                        .map(result -> verifySignatureData(result, orderId))
                        .orElse(false);
            });
        } catch (Exception e) {
            System.err.println("Error in verifyOrderSignature: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean verifySignatureData(Map<String, Object> result, String orderId) {
        try {
            System.out.println("\nProcessing data for order: " + orderId);
            result.forEach((key, value) -> System.out.println(key + ": " + (value != null ? value : "null")));

            String signatureBase64 = findValueCaseInsensitive(result, "signature");
            String publicKeyBase64 = findValueCaseInsensitive(result, "public_key");
            String orderData = findValueCaseInsensitive(result, "orderData");
            String address = findValueCaseInsensitive(result, "address");
            String phone = findValueCaseInsensitive(result, "numberPhone");

            if (signatureBase64 == null || publicKeyBase64 == null || orderData == null) {
                System.out.println("Missing required verification data");
                return false;
            }

            String hashedAddress = hashString(address);
            String hashedPhone = hashString(phone);
            String completeData = orderData + hashedAddress + hashedPhone;

            System.out.println("Order Data: " + orderData);
            System.out.println("Complete Data: " + completeData);

            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(completeData.getBytes(StandardCharsets.UTF_8));

            boolean isValid = signature.verify(signatureBytes);
            System.out.println("Verification result: " + isValid);
            return isValid;

        } catch (Exception e) {
            System.err.println("Error in verifySignatureData: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private String findValueCaseInsensitive(Map<String, Object> map, String key) {
        return map.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(key))
                .findFirst()
                .map(entry -> entry.getValue())
                .map(Object::toString)
                .orElse(null);
    }

    private String hashString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing string: " + e.getMessage());
            return input;
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
        ServletSignatureVerify si = new ServletSignatureVerify();
        boolean iv = si.verifyOrderSignature("OR01");
        System.out.println( iv);
    }
}
