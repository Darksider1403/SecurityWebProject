import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private int userId;
    private JTable ordersTable;
    private DefaultTableModel model;

    public LoginForm() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.NORTH);
        mainPanel.add(loginButton, BorderLayout.SOUTH);

        add(mainPanel);
        getRootPane().setDefaultButton(loginButton);
    }

    private String encryptMd5(String password) {
        String hash = "MaNdifksgkjsdngHagl0128379lkjs@.";
        password = hash + password;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(password.getBytes());
            BigInteger bigInt = new BigInteger(1, b);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        String encryptedPassword = encryptMd5(password);

        try {
            if (validateLogin(username, encryptedPassword)) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Login successful!");

                // Open OrdersViewer with the current userId
                SwingUtilities.invokeLater(() -> {
                    OrdersViewer ordersViewer = new OrdersViewer(userId);
                    ordersViewer.setVisible(true);
                    this.dispose(); // Close login window
                });
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Invalid username or password");
                passwordField.setText("");
            }
        } catch (SQLException ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean validateLogin(String username, String encryptedPassword) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/project_ltw";
        String dbUser = "root";
        String dbPassword = "";

        String sql = "SELECT id FROM accounts WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, encryptedPassword);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id"); // Store user ID
                    return true;
                }
                return false;
            }
        }
    }

    private void showOrders() {
        JFrame ordersFrame = new JFrame("Unverified Orders");
        ordersFrame.setSize(800, 600);
        ordersFrame.setLocationRelativeTo(null);
        ordersFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create table model with your specific columns
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("#");
        model.addColumn("ID");
        model.addColumn("Address");
        model.addColumn("Phone Number");
        model.addColumn("Status");
        model.addColumn("Date Buy");
        model.addColumn("Date Arrival");
        model.addColumn("idAccount");
        model.addColumn("Is Verified");

        JTable ordersTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(ordersTable);

        // Adjust column widths
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(8).setPreferredWidth(80);

        try {
            loadOrders(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ordersFrame,
                    "Error loading orders: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        ordersFrame.add(scrollPane);
        ordersFrame.setVisible(true);
    }

    private void loadOrders(DefaultTableModel model) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/project_ltw";
        String dbUser = "root";
        String dbPassword = "";

        String sql = "SELECT * FROM orders WHERE idAccount = ? AND is_verified = 0 ORDER BY dateBuy DESC";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
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
                            rs.getInt("is_verified")
                    });
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}