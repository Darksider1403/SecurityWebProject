

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
import java.nio.file.*;

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
                return column == 9;
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
        ordersTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));

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
            // Remove the AND is_verified = 0 condition
            String sql = "SELECT * FROM orders WHERE idAccount = ? ORDER BY dateBuy DESC";

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();

                int rowNum = 1;
                while (rs.next()) {
                    boolean isVerified = rs.getInt("is_verified") == 1;
                    model.addRow(new Object[]{
                            rowNum++,
                            rs.getString("id"),
                            rs.getString("address"),
                            rs.getString("numberPhone"),
                            rs.getInt("status"),
                            rs.getDate("dateBuy"),
                            rs.getDate("dateArrival"),
                            rs.getInt("idAccount"),
                            isVerified ? "Verified" : "Unverified",
                            isVerified ? "Verified" : "Verify Order" // Change button text based on status
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
            e.printStackTrace();
            insertLog(userId, "127.0.0.1", "ERROR", "Error loading orders", "Failed", 0, "LOAD_ERROR");
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String orderId;
        private boolean clicked;
        private final OrdersViewer parent;

        public ButtonEditor(JCheckBox checkBox, OrdersViewer parent) {
            super(checkBox);
            this.parent = parent;
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
            String isVerifiedStr = (String) table.getValueAt(row, 8);

            button.setText((value == null) ? "" : value.toString());
            button.setEnabled(!"Verified".equals(isVerifiedStr));

            if ("Verified".equals(isVerifiedStr)) {
                button.setBackground(new Color(200, 200, 200));
                button.setForeground(Color.GRAY);
            } else {
                button.setBackground(UIManager.getColor("Button.background"));
                button.setForeground(UIManager.getColor("Button.foreground"));
            }

            clicked = false;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                parent.verifyOrder(orderId);
            }
            return button.getText();
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
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

            // Disable button if order is verified
            String isVerifiedStr = (String) table.getValueAt(row, 8);
            setEnabled(!"Verified".equals(isVerifiedStr));

            if ("Verified".equals(isVerifiedStr)) {
                setBackground(new Color(200, 200, 200));
                setForeground(Color.GRAY);
            } else {
                setBackground(UIManager.getColor("Button.background"));
                setForeground(UIManager.getColor("Button.foreground"));
            }

            return this;
        }
    }

    protected void verifyOrder(String orderId) {
        try {
            JDialog dialog = new JDialog(this, "Verify Order: " + orderId, true);
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

            JTextField filePathField = new JTextField(30);
            filePathField.setEditable(false);
            JButton browseButton = new JButton("Browse Private Key File");
            JButton verifyButton = new JButton("Sign");

            browseButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            });

            verifyButton.addActionListener(e -> {
                try {
                    String privateKey = new String(Files.readAllBytes(Paths.get(filePathField.getText())));
                    if (verifyAndSaveSignature(orderId, privateKey)) {
                        JOptionPane.showMessageDialog(dialog, "Order verified successfully!");
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Invalid signature!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Verification failed: " + ex.getMessage());
                }
            });

            gbc.gridx = 0;
            gbc.gridy = 0;
            signPanel.add(new JLabel("Private Key File:"), gbc);
            gbc.gridx = 1;
            signPanel.add(filePathField, gbc);
            gbc.gridx = 2;
            signPanel.add(browseButton, gbc);
            gbc.gridx = 3;
            signPanel.add(verifyButton, gbc);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
            mainPanel.add(signPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during order verification: " + e.getMessage());
        }
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

    private boolean verifyAndSaveSignature(String orderId, String privateKey) {
        Connection conn = null;
        try {
            // Get the order details for logging
            String orderDetails = getOrderDataForSigning(orderId);
            String publicKeyStr = getPublicKey(userId);

//            if (publicKeyStr == null || !verifyKeys(privateKey, publicKeyStr)) {
//                // Log failed verification attempt
//                insertLog(userId,
//                        getClientIP(),
//                        0,  // ERROR level
//                        "Order Verification",
//                        orderId,
//                        Base64.getEncoder().encodeToString(orderDetails.getBytes()),
//                        null,
//                        0); // Failed status
//                return false;
//            }

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

//            insertLog(userId,
//                    getClientIP(),
//                    1,  // INFO level
//                    "Order Verification",
//                    orderId,
//                    Base64.getEncoder().encodeToString(orderDetails.getBytes()),
//                    null,
//                    1); // Success status

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
        String sql = "SELECT od.idProduct, od.quantity, od.price, o.address, o.numberPhone " +
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
                double price = rs.getDouble("price");
                String address = rs.getString("address");
                String numberPhone = rs.getString("numberPhone");

                String hashedAddress = hashString(address);
                String hashedPhone = hashString(numberPhone);

                System.out.println("Adding to signature data:");
                System.out.println("Product: " + product);
                System.out.println("Quantity: " + quantity);
                System.out.println("Price: " + price);
                System.out.println("Hashed Address: " + hashedAddress);
                System.out.println("Hashed Phone: " + hashedPhone);

                data.append(product)
                        .append(quantity)
                        .append(price)
                        .append(hashedAddress)
                        .append(hashedPhone);
            }

            System.out.println("\nFinal string to be signed: " + data.toString());
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
        String sql = "INSERT INTO log ( ip, level, address, preValue, value, date, country, status) " +
                "VALUES ( ?, ?, ?, ?, ?, CURRENT_DATE, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ip);
            pstmt.setInt(2, level);
            pstmt.setString(3, address);
            pstmt.setString(4, preValue);
            pstmt.setString(5, value);
            pstmt.setString(6, country != null ? country : "Unknown");
            pstmt.setInt(7, status);

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



