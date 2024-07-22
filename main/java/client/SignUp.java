package client;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.sql.Connection;


public class SignUp {
    private static JFrame app;
    private static JTextField username_entry;
    private static JPasswordField password_entry;
    private static Connection conn;


    public SignUp(Connection conn) {
        this.conn=conn;

        app = new JFrame();
        app.setTitle("Signup");
        app.setUndecorated(true);
        app.setSize(650, 470);
        app.getContentPane().setBackground(new Color(0, 18, 32));
        app.setLayout(null); // Using absolute layout
        app.setLocationRelativeTo(null);

        JPanel titleBarPanel = new JPanel();
        titleBarPanel.setBackground(new Color(38, 80, 112));
        titleBarPanel.setBounds(0, 0, 650, 30);
        titleBarPanel.setLayout(null);
        app.add(titleBarPanel);

        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setForeground(Color.CYAN);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        titleLabel.setBounds(10, 0, 100, 30);
        titleBarPanel.add(titleLabel);

        Font font1 = new Font("Helvetica", Font.BOLD, 25);
        Font font2 = new Font("Arial", Font.BOLD, 17);
        Font font3 = new Font("Arial", Font.BOLD, 13);
        Font font4 = new Font("Arial", Font.BOLD | Font.ITALIC, 17);


        try {
            // Hardcoded icon image data
            FileInputStream inputStream = new FileInputStream("C:/Users/dobri/OneDrive/Desktop/demo/src/main/resources/static/close1.png");
            byte[] closeIconData = inputStream.readAllBytes();
            inputStream.close();

            // Create ImageIcon from the hardcoded image data
            ImageIcon closeIcon = new ImageIcon(closeIconData);

            JButton closeButton = new JButton();
            closeButton.setIcon(closeIcon);
            closeButton.setFocusPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setBounds(620, 0, 30, 30);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); // Close the application
                }
            });
            titleBarPanel.add(closeButton);

            // Load image file from absolute file path
            inputStream = new FileInputStream("C:/Users/dobri/OneDrive/Desktop/demo/src/main/resources/static/sea.gif");
            byte[] imageData = inputStream.readAllBytes();
            inputStream.close();

            // Create ImageIcon from byte array
            ImageIcon image1 = new ImageIcon(imageData);
            JLabel image1_label = new JLabel(image1);
            image1_label.setBounds(0, 0, image1.getIconWidth(), image1.getIconHeight());
            app.add(image1_label);
        } catch (Exception e) {
            System.out.println("Error loading image: " + e);
        }

        JLabel signup_label = new JLabel("Sign up");
        signup_label.setFont(font1);
        signup_label.setForeground(Color.CYAN);
        signup_label.setBounds(400, 40, 200, 50);
        app.add(signup_label);

        username_entry = new JTextField();
        username_entry.setFont(font2);
        username_entry.setForeground(Color.CYAN);
        username_entry.setBackground(new Color(18, 17, 17));
        username_entry.setBorder(BorderFactory.createLineBorder(new Color(0, 71, 128), 3));
        username_entry.setBounds(400, 100, 200, 50);
        app.add(username_entry);

        password_entry = new JPasswordField();
        password_entry.setFont(font2);
        password_entry.setForeground(Color.CYAN);
        password_entry.setBackground(new Color(18, 17, 17));
        password_entry.setBorder(BorderFactory.createLineBorder(new Color(0, 71, 128), 3));
        password_entry.setBounds(400, 170, 200, 50);
        app.add(password_entry);

        JButton signup_button = new JButton("Sign up");
        signup_button.setFont(font2);
        signup_button.setForeground(Color.DARK_GRAY);
        signup_button.setBackground(new Color(39, 220, 199));
        signup_button.setBounds(400, 240, 120, 40);
        app.add(signup_button);

        JLabel login_label = new JLabel("Already have an account?");
        login_label.setFont(font3);
        login_label.setForeground(Color.gray);
        login_label.setBounds(400, 320, 200, 20);
        app.add(login_label);

        JButton login_button = new JButton("Login");
        login_button.setFont(font4);
        login_button.setForeground(new Color(39, 220, 199));
        login_button.setBackground(new Color(0, 18, 32));
        login_button.setBounds(400, 349, 80, 30);
        app.add(login_button);

        signup_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = username_entry.getText();
                String password = new String(password_entry.getPassword());
                // Call signup method here
                signup(username, password);
            }
        });

        login_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call the login method from LogIn class
                LogIn login = new LogIn();
                try {
                    login.login();// Make sure login() method exists in LogIn class
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                app.dispose();// Close the sign-up window
            }
        });
        app.setVisible(true);
    }
    static Statement stmt;
    static PreparedStatement pstmt;
    static ResultSet rs;

    public static void signup(String username, String password) {
        if (!username.isEmpty() && !password.isEmpty()) {
            try {
                // Create USERS table if not exists
                createUsersTable();

                // Check if the username already exists
                if (!isUsernameExists(username)) {
                    // Hash the password
                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());;

                    // Insert user into the USERS table
                    insertUser(username, hashedPassword);



                    JOptionPane.showMessageDialog(null, "Account has been created.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Enter all data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static void createUsersTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS USERS (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT NOT NULL)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    private static boolean isUsernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM USERS WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void insertUser(String username, String hashedPassword) throws SQLException {
        String sql = "INSERT INTO USERS (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
        }
    }


}



