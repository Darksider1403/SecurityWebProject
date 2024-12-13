<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Verify Order Signature</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 20px;
        }
        h2 {
            color: #333;
        }
        .form-group {
            margin-bottom: 15px;
        }
        input[type="text"] {
            padding: 8px;
            width: 200px;
        }
        button {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        button:hover {
            background-color: #45a049;
        }
        .response {
            margin-top: 20px;
            font-size: 1.2em;
        }
        .error {
            color: red;
        }
        .success {
            color: green;
        }
    </style>
</head>
<body>

<h2>Verify Order Signature</h2>

<form action="./ServletSignatureVerify" method="post">
    <div class="form-group">
        <label for="orderId">Order ID:</label>
        <input type="text" id="orderId" name="orderId" required>
    </div>
    <button type="submit">Verify Signature</button>
</form>

<%-- Display response message if available --%>
<c:if test="${not empty param.resultMessage}">
    <div class="response ${param.resultStatus}">
            ${param.resultMessage}
    </div>
</c:if>

</body>
</html>
