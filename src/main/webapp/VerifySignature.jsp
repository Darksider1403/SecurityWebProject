<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.security.*" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.security.spec.X509EncodedKeySpec" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Verify Digital Signature</title>
  <link rel="stylesheet" href="styles.css">
</head>
<body>
<div class="container">
  <h1>Verify Digital Signature</h1>

  <!-- Input Form -->
  <form action="VerifySignature.jsp" method="POST">
    <label for="orderId">Enter Order ID:</label>
    <input type="text" id="orderId" name="orderId" required>
    <button type="submit">Verify</button>
  </form>

  <hr>

  <%
    if (request.getMethod().equalsIgnoreCase("POST")) {
      String orderId = request.getParameter("orderId");

      // Database connection and query (Adjust connection settings as needed)
      String url = "jdbc:mysql://localhost:3306/your_database";
      String user = "your_username";
      String password = "your_password";

      Connection connection = null;
      PreparedStatement statement = null;
      ResultSet resultSet = null;

      try {
        // Connect to the database
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(url, user, password);

        // Fetch order details
        String query = "SELECT * FROM orders WHERE order_id = ?";
        statement = connection.prepareStatement(query);
        statement.setString(1, orderId);
        resultSet = statement.executeQuery();

        if (resultSet.next()) {
          String orderDetails = resultSet.getString("details");
          String publicKeyStr = resultSet.getString("public_key");
          String signatureStr = resultSet.getString("signature");

          // Display the order details
  %>
  <h2>Order Details</h2>
  <p><strong>Order ID:</strong> <%= orderId %></p>
  <p><strong>Details:</strong> <%= orderDetails %></p>
  <p><strong>Public Key:</strong> <%= publicKeyStr %></p>
  <p><strong>Signature:</strong> <%= signatureStr %></p>

  <%
    // Verify the signature
    try {
      PublicKey publicKey = KeyFactory.getInstance("RSA")
              .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)));

      Signature signature = Signature.getInstance("SHA256withRSA");
      signature.initVerify(publicKey);
      signature.update(orderDetails.getBytes());

      boolean isVerified = signature.verify(Base64.getDecoder().decode(signatureStr));
  %>
  <p><strong>Verification Result:</strong> <%= isVerified ? "Valid" : "Invalid" %></p>
  <%
    } catch (Exception e) {
      System.out.println("<p style='color:red;'>Error verifying signature: " + e.getMessage() + "</p>");
    }
  } else {
  %>
  <p style="color:red;">Order not found!</p>
  <%
        }
      } catch (Exception e) {
        System.out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
      } finally {
        try {
          if (resultSet != null) resultSet.close();
          if (statement != null) statement.close();
          if (connection != null) connection.close();
        } catch (SQLException e) {
          System.out.println("<p style='color:red;'>Error closing resources: " + e.getMessage() + "</p>");
        }
      }
    }
  %>
</div>
</body>
</html>
