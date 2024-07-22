package client;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import static gesicht.MainApp.showMainApp;


public class LogIn {

    private static Connection conn;

    public static void login() throws IOException {
        Font font1 = new Font("Helvetica", Font.BOLD, 25);
        Font font2 = new Font("Arial", Font.BOLD, 17);

        JFrame frame = new JFrame("Log in");
        frame.setUndecorated(true); // Set to undecorated to create custom title bar
        frame.setSize(860, 430);
        frame.getContentPane().setBackground(new Color(199, 5, 96));
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        JPanel titleBarPanel = new JPanel();
        titleBarPanel.setBackground(new Color(54, 3, 26));
        titleBarPanel.setBounds(0, 0, 900, 30);
        titleBarPanel.setLayout(null);
        frame.add(titleBarPanel);

        JLabel titleLabel = new JLabel("Log In");
        titleLabel.setForeground(Color.PINK);
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 18));
        titleLabel.setBounds(10, 0, 100, 30);
        titleBarPanel.add(titleLabel);



        JPanel frame2 = new JPanel();
        frame2.setLayout(null);
        frame2.setBackground(new Color(84, 1, 40));
        frame2.setSize(900, 500);

        try {
            // Hardcoded icon image data
            FileInputStream inputStream = new FileInputStream("C:/Users/dobri/OneDrive/Desktop/demo/src/main/resources/static/close.png");
            byte[] closeIconData = inputStream.readAllBytes();
            inputStream.close();

            // Create ImageIcon from the hardcoded image data
            ImageIcon closeIcon = new ImageIcon(closeIconData);

            JButton closeButton = new JButton();
            closeButton.setIcon(closeIcon);
            closeButton.setFocusPainted(false);
            closeButton.setContentAreaFilled(false);
            closeButton.setBounds(830, 0, 30, 30);
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); // Close the application
                }
            });
            titleBarPanel.add(closeButton);

            // Load image file from absolute file path
            inputStream = new FileInputStream("C:/Users/dobri/OneDrive/Desktop/demo/src/main/resources/static/phoenix.png");
            byte[] imageData = inputStream.readAllBytes();
            inputStream.close();

            // Create ImageIcon from byte array
            ImageIcon image1 = new ImageIcon(imageData);
            JLabel image1_label = new JLabel(image1);
            image1_label.setBounds(0, 0, 500, 450);
            frame2.add(image1_label);
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading image: " + e.getMessage(), "Image Error", JOptionPane.ERROR_MESSAGE);
        }

        JLabel login_label2 = new JLabel("Log in");
        login_label2.setFont(font1);
        login_label2.setForeground(Color.PINK);
        login_label2.setBounds(550, 20, 200, 50);
        frame2.add(login_label2);

        JTextField username_entry2 = new JTextField();
        username_entry2.setFont(font2);
        username_entry2.setForeground(Color.PINK);
        username_entry2.setBackground(new Color(47, 2, 2));
        username_entry2.setBorder(BorderFactory.createLineBorder(new Color(196, 15, 66), 3));
        username_entry2.setBounds(550, 95, 300, 50);
        frame2.add(username_entry2);

        JPasswordField password_entry2 = new JPasswordField();
        password_entry2.setFont(font2);
        password_entry2.setForeground(Color.PINK);
        password_entry2.setBackground(new Color(47, 2, 2));
        password_entry2.setBorder(BorderFactory.createLineBorder(new Color(196, 15, 66), 3));
        password_entry2.setBounds(550, 165, 300, 50);
        frame2.add(password_entry2);

        JButton login_button2 = new JButton("Log in");
        login_button2.setFont(font2);
        login_button2.setForeground(Color.PINK); // Change button foreground color to white
        login_button2.setBackground(new Color(47, 2, 2));
        login_button2.setBounds(550, 235, 200, 40);
        frame2.add(login_button2);

        JButton signup_button2 = new JButton("Sign up");
        signup_button2.setFont(font2);
        signup_button2.setForeground(Color.PINK); // Change button foreground color to white
        signup_button2.setBackground(new Color(47, 2, 2));
        signup_button2.setBounds(550, 305, 200, 40);
        frame2.add(signup_button2);

        signup_button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call the login method from LogIn class
                SignUp signup = new SignUp(conn);
                frame.dispose();
                frame.setVisible(false);
            }
        });

        login_button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginAccount(username_entry2.getText(), new String(password_entry2.getPassword()), frame);
            }
        });

        frame.getContentPane().add(frame2);
        frame.setVisible(true); // Set frame visibility to true
    }


    public static void loginAccount(String username, String password, JFrame frame) {
        if (!username.isEmpty() && !password.isEmpty()) {
            int userId = authenticateUser(username, password);

            if (userId != -1) {

                // Create and display the main frame
                EventQueue.invokeLater(() -> {
                    try {
                        showMainApp("username");
                        frame.dispose();
                        frame.setVisible(false);
                    } catch (IOException e) {
                        new RuntimeException(e);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static PreparedStatement pstmt;
    private static ResultSet rs;


    public static int authenticateUser(String username, String password) {
        try {
            pstmt = DatabaseManager.conn.prepareStatement("SELECT rowid, password FROM USERS WHERE username=?");
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt(1); // Use 1 to reference the first column, which is the rowid
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return userId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
