import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.*;
import java.sql.*;
import java.sql.Timestamp;
import java.util.Base64;
import java.time.LocalDateTime;

public class OrdersViewer extends JFrame {
    private final JTable ordersTable;
    private final DefaultTableModel model;
    private final int userId;
    private final String DB_URL = "jdbc:mysql://localhost:3306/project_ltw";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";

    public OrdersViewer(int userId) {
        this.userId = userId;
        setTitle("Unverified Orders");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Only allow editing for verify button column
            }
        };

        model.addColumn("#");
        model.addColumn("ID");
        model.addColumn("Address");
        model.addColumn("Phone Number");
        model.addColumn("Status");
        model.addColumn("Date Buy");
        model.addColumn("Date Arrival");
        model.addColumn("idAccount");
        model.addColumn("Is Verified");
        model.addColumn("Action");

        ordersTable = new JTable(model);
        ordersTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        ordersTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(ordersTable);

        // Set column widths
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // #
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // ID
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Address
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Status
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Date Buy
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Date Arrival
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // idAccount
        ordersTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Is Verified
        ordersTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Action

        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(refreshButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        loadOrders();
    }

    private void loadOrders() {
        model.setRowCount(0);
        try {
            String sql = "SELECT * FROM orders WHERE idAccount = ? AND is_verified = 0 ORDER BY dateBuy DESC";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                int rowNum = 1;
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rowNum++,
                            rs.getString("id"),
                            rs.getString("address"),
                            rs.getString("numberPhone"),
                            rs.getInt("status"),
                            rs.getDate("dateBuy"),
                            rs.getDate("dateArrival"),
                            rs.getInt("idAccount"),
                            rs.getInt("is_verified"),
                            "Sign Order"
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
            e.printStackTrace();
            insertLog(userId, "127.0.0.1", "ERROR", "Error loading orders", "Failed", 0, "LOAD_ERROR");
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String orderId;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                clicked = true;
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            orderId = (String) table.getValueAt(row, 1);
            button.setText((value == null) ? "" : value.toString());
            clicked = false;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                signOrder(orderId);
            }
            return "Sign Order";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    private void signOrder(String orderId) {
        JDialog dialog = new JDialog(this, "Sign Order: " + orderId, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        DefaultTableModel productModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productModel.addColumn("Product ID");
        productModel.addColumn("Quantity");
        productModel.addColumn("Price");
        productModel.addColumn("Total");

        JTable productTable = new JTable(productModel);
        loadOrderDetails(orderId, productModel);

        JPanel signPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextArea privateKeyArea = new JTextArea(3, 40);
        privateKeyArea.setLineWrap(true);
        JScrollPane keyScroll = new JScrollPane(privateKeyArea);

        JButton signButton = new JButton("Sign");

        gbc.gridx = 0;
        gbc.gridy = 0;
        signPanel.add(new JLabel("Enter Private Key:"), gbc);
        gbc.gridx = 1;
        signPanel.add(keyScroll, gbc);
        gbc.gridx = 2;
        signPanel.add(signButton, gbc);

        signButton.addActionListener(e -> {
            String privateKey = privateKeyArea.getText().trim();
            try {
                if (signAndSaveSignature(orderId, privateKey)) {
                    JOptionPane.showMessageDialog(dialog, "Order verified successfully!");
                    dialog.dispose();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Invalid signature!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Verification failed: " + ex.getMessage());
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        mainPanel.add(signPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private String getPublicKey(int userId) throws SQLException {
        String sql = "SELECT public_key FROM user_keys WHERE user_id = ? AND is_active = true";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("public_key");
            }
        }
        return null;
    }

    private boolean verifyKeys(String privateKeyStr, String publicKeyStr) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                    Base64.getDecoder().decode(privateKeyStr.trim()));
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                    Base64.getDecoder().decode(publicKeyStr.trim()));
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            byte[] testData = "Test verification data".getBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(testData);
            byte[] signedData = signature.sign();

            signature.initVerify(publicKey);
            signature.update(testData);
            return signature.verify(signedData);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateOrderVerification(String orderId) throws SQLException {
        String sql = "UPDATE orders SET is_verified = 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            pstmt.executeUpdate();
        }
    }

    private void insertLog(int userId, String ip, String level, String message, String status, int value, String preValue) {
        String sql = "INSERT INTO log (id, ip, level, address, preValue, value, date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, ip);
            pstmt.setInt(3, level.equals("INFO") ? 1 : 0);
            pstmt.setString(4, "Order Verification");
            pstmt.setString(5, preValue);
            pstmt.setInt(6, value);
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(8, status);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        loadOrders();
    }

    private boolean signAndSaveSignature(String orderId, String privateKey) {
        Connection conn = null;
        try {
            // Get the order details for logging
            String orderDetails = getOrderDataForSigning(orderId);
            System.out.println(orderDetails);
            String publicKeyStr = getPublicKey(userId);
            if (publicKeyStr == null || !verifyKeys(privateKey, publicKeyStr)) {
                // Log failed verification attempt
                insertLog(userId,
                        getClientIP(),
                        0,  // ERROR level
                        "Order Verification",
                        orderId,
                        Base64.getEncoder().encodeToString(orderDetails.getBytes()),
                        null,
                        0); // Failed status
                return false;
            }

            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);

            // Get active key ID
            int keyId;
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT id FROM user_keys WHERE user_id = ? AND is_active = 1")) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (!rs.next()) return false;
                keyId = rs.getInt("id");
            }

            // Create and save signature
            byte[] signedData = signData(orderDetails, privateKey);
            String signature = Base64.getEncoder().encodeToString(signedData);

            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO signature (id_order, id_user, id_key, Signature, CreatedAt) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)")) {
                pstmt.setString(1, orderId);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, keyId);
                pstmt.setString(4, signature);
                pstmt.executeUpdate();
            }

            // Update order verification status
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE orders SET is_verified = 1 WHERE id = ?")) {
                pstmt.setString(1, orderId);
                pstmt.executeUpdate();
            }

            // Log successful verification
            insertLog(userId,
                    getClientIP(),
                    1,  // INFO level
                    "Order Verification",
                    orderId,
                    Base64.getEncoder().encodeToString(orderDetails.getBytes()),
                    null,
                    1); // Success status

            conn.commit();
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getOrderDataForSigning(String orderId) throws SQLException {
        StringBuilder data = new StringBuilder();
        String sql = "SELECT od.idProduct, od.quantity, ROUND(od.price, 0) as price, o.address, o.numberPhone " +
                "FROM order_details od " +
                "JOIN orders o ON o.id = od.idOrder " +
                "WHERE od.idOrder = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String product = rs.getString("idProduct");
                int quantity = rs.getInt("quantity");
                int price = rs.getInt("price");  // Changed to getInt since we ROUND in SQL
                String address = rs.getString("address");
                String numberPhone = rs.getString("numberPhone");

                String hashedAddress = hashString(address);
                String hashedPhone = hashString(numberPhone);

                data.append(product)
                        .append(quantity)
                        .append(price)  // No .0 will be appended since it's an integer
                        .append(hashedAddress)
                        .append(hashedPhone);
            }
        }
        return data.toString();
    }
    private String hashString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
    private byte[] signData(String data, String privateKeyStr) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(privateKeyStr.trim())
        );
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        return signature.sign();
    }

    private void loadOrderDetails(String orderId, DefaultTableModel model) {
        String sql = "SELECT od.idProduct, od.quantity, od.price " +
                "FROM order_details od " +
                "WHERE od.idOrder = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                model.addRow(new Object[]{
                        rs.getString("idProduct"),
                        quantity,
                        price,
                        price * quantity
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertLog(int userId, String ip, int level, String address,
                           String preValue, String value, String country, int status) {
        String sql = "INSERT INTO log (id, ip, level, address, preValue, value, date, country, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, ip);
            pstmt.setInt(3, level);
            pstmt.setString(4, address);
            pstmt.setString(5, preValue);
            pstmt.setString(6, value);
            pstmt.setString(7, country != null ? country : "Unknown");
            pstmt.setInt(8, status);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getClientIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

}