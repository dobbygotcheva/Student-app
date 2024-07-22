package gesicht;

import client.LogIn;
import todo.ToDoApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class MainApp {

    private static Home home;

    public static void showMainApp(String username) throws IOException {
        JFrame root = new JFrame();
        root.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        root.setUndecorated(true); // Remove window decorations
        root.setSize(1300, 768); // Set size
        root.setLocationRelativeTo(null); // Center window on screen

        // Initialize Home panel
        home = new Home();
        root.setContentPane(home);

        // Create a panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.BLACK);
        controlPanel.setLayout(new FlowLayout());
        root.add(controlPanel, BorderLayout.SOUTH);

        String[] options = {"To do app", "Chat"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        comboBox.setBackground(new Color(142, 214, 224));
        comboBox.addActionListener(e -> {
            String choice = (String) comboBox.getSelectedItem();
            System.out.println("Combobox selection: " + choice);
        });
        comboBox.setPreferredSize(new Dimension(150, 30)); // Set preferred size
        controlPanel.add(comboBox);

        // Create and configure the "Go there!" button
        JButton myButton = new JButton("Go there!");
        myButton.setBackground(new Color(142, 171, 224));
        myButton.addActionListener(e -> {
            try {
                String selectedOption = (String) comboBox.getSelectedItem();
                // Call the method to show the selected page
                showSelectedPage(username, selectedOption, root);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        myButton.setPreferredSize(new Dimension(120, 25)); // Set preferred size
        controlPanel.add(myButton);

        // Create and configure the "Log out" button
        JButton myButton2 = new JButton("Log out");
        myButton2.setBackground(new Color(142, 171, 224));
        myButton2.addActionListener(e -> {
            try {
                // Call the login method
                LogIn.login();
                home.stop();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            root.dispose();
        });
        myButton2.setPreferredSize(new Dimension(120, 25)); // Set preferred size
        controlPanel.add(myButton2);

        // Add window listener to start/stop Home animation
        root.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                home.play(0);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                home.stop();
            }
        });

        // Make the frame visible
        root.setVisible(true);
    }

    public static void showSelectedPage(String username, String selectedOption, JFrame root) throws SQLException, IOException {
        if (selectedOption.equals("To do app")) {
            new ToDoApp(username);
            System.out.println("Opening To Do App");
            // Dispose the main app window when navigating to the to-do app
            home.stop();
            root.dispose();
        } else if (selectedOption.equals("Chat")) {
            try {
                // Then direct the browser to the chat page
                Desktop.getDesktop().browse(new URI("http://localhost:8080/chat"));
            } catch (IOException | URISyntaxException e) {
                // Handle the exception
                e.printStackTrace();
            }
        }
    }
}

