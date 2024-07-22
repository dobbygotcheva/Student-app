package todo;

import gesicht.MainApp;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;


public class ToDoApp {
    private JFrame frame;
    private JList<String> list;
    private JButton addButton;
    private JButton deleteButton;
    private JButton crossOffButton;
    private JButton uncrossButton;
    private JTextField entry;
    private JLabel imageLabel;
    private Connection conn;
    private String username;

    public ToDoApp(String username) throws SQLException, IOException {
        this.username = username;
        conn = DriverManager.getConnection("jdbc:sqlite:C:/Users/dobri/OneDrive/Desktop/senior_project/src/main/java/todo/todo.db");


        createTodoListTable(username);

        frame = new JFrame("To Do App");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    // Call the method to go back to the MainApp
                    MainApp.showMainApp(username);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (list.getSelectedIndex() == -1) {
                        deleteButton.setEnabled(false);
                        crossOffButton.setEnabled(false);
                        uncrossButton.setEnabled(false);
                    } else {
                        deleteButton.setEnabled(true);
                        crossOffButton.setEnabled(true);
                        uncrossButton.setEnabled(true);
                    }
                }
            }
        });


        addButton = new JButton("Add Item");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String item = entry.getText().trim();
                if (!item.isEmpty()) {
                    try {
                        addItem(item);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });



        deleteButton = new JButton("Delete Item");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = list.getSelectedValue();
                if (selectedItem != null) {
                    try {
                        deleteItem(selectedItem);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        crossOffButton = new JButton("Cross Off");
        crossOffButton.setEnabled(false);
        crossOffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = list.getSelectedValue();
                if (selectedItem != null) {
                    try {
                        crossOffItem(selectedItem);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        uncrossButton = new JButton("Uncross");
        uncrossButton.setEnabled(false);
        uncrossButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = list.getSelectedValue();
                if (selectedItem != null) {
                    try {
                        uncrossItem(selectedItem);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        Color pinkColor = new Color(232, 207, 228);
        entry = new JTextField(30); // Increase text field length
        entry.setBackground(pinkColor); // Set background color for text field

// Set font to Georgia Italics
        Font font = new Font("Georgia", Font.ITALIC, 16);
        entry.setFont(font);

// Set background color for list box
        list.setBackground(pinkColor);

// Set font to Georgia Italics for list items
        Font font1 = new Font("Georgia", Font.ITALIC, 20);
        list.setFont(font1);

        // Load image file from absolute file path
        FileInputStream inputStream = new FileInputStream("C:/Users/dobri/OneDrive/Desktop/demo/src/main/resources/static/pinko.png");
        byte[] imageData = inputStream.readAllBytes();
        inputStream.close();

        // Create ImageIcon from byte array
        ImageIcon imageIcon = new ImageIcon(imageData);
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Increase image size
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledImageIcon);
        frame.add(imageLabel);


// Set background color for buttons
        Color buttonColor = new Color(239, 184, 229); // Light pink color
        addButton.setBackground(buttonColor);
        deleteButton.setBackground(buttonColor);
        crossOffButton.setBackground(buttonColor);
        uncrossButton.setBackground(buttonColor);

// Set font to Georgia Italics for list items
        list.setFont(font);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(crossOffButton);
        buttonPanel.add(uncrossButton);


        Color lightColor = new Color(229, 143, 210);
        JPanel entryPanel = new JPanel(); // Initialize entryPanel
        entryPanel.setBackground(lightColor); // Set background color for entryPanel
        entryPanel.add(entry);

        JPanel imagePanel = new JPanel(); // Initialize imagePanel
        imagePanel.setBackground(lightColor);// Set background color for imagePanel

        imagePanel.add(imageLabel);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(entryPanel, BorderLayout.NORTH);
        controlPanel.add(buttonPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), imagePanel);
        splitPane.setDividerLocation(200);
        splitPane.setOneTouchExpandable(true);

        frame.getContentPane().setBackground(pinkColor); // Set background color to light pink
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        loadTasksForUser(username);


    }

    private void createTodoListTable(String username) throws SQLException {
        String tableName = username + "_todo_list";
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "item TEXT," +
                "complete BOOLEAN DEFAULT 0)";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }



    private void populateListBox() throws SQLException {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        String tableName = username + "_todo_list";
        String sql = "SELECT item, complete FROM " + tableName;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getString("item");
                    boolean complete = rs.getBoolean("complete");
                    if (!complete) {
                        listModel.addElement(item); // Add only if not complete
                    } else {
                        // Check if the item is not marked as complete and then deleted
                        if (!isItemDeleted(item)) {
                            item = "<html><strike>" + item + "</strike></html>"; // Apply HTML formatting for completed items
                            listModel.addElement(item);
                        }
                    }
                }
            }
        }
        list.setModel(listModel);
    }

    // Method to check if an item has been marked as complete and then deleted
    private boolean isItemDeleted(String item) throws SQLException {
        String tableName = username + "_todo_list";
        String sql = "SELECT count(*) AS count FROM " + tableName + " WHERE item=? AND complete=1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }




    // Method to add a task for the current user and save it to the database
    private void addItem(String item) throws SQLException {
        String sql = "INSERT INTO " + username + "_todo_list (item, complete) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item);
            pstmt.setBoolean(2, false); // Set the completion status to false initially
            pstmt.executeUpdate();
            entry.setText("");
            populateListBox();
        }
    }


    // Method to cross off an item in the list box and update its completion status in the database
    private void crossOffItem(String item) throws SQLException {
        DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
        int index = listModel.indexOf(item);
        if (index != -1) {
            String crossedItem = listModel.getElementAt(index);
            if (!crossedItem.startsWith("<html><strike>")) {
                crossedItem = "<html><strike>" + crossedItem + "</strike></html>";
                listModel.set(index, crossedItem);
                // Update the completion status in the database
                String sql = "UPDATE " + username + "_todo_list SET complete=1 WHERE item=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, item);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    private void uncrossItem(String item) throws SQLException {
        DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
        int index = listModel.indexOf(item);
        if (index != -1) {
            String uncrossedItem = listModel.getElementAt(index);
            if (uncrossedItem.startsWith("<html><strike>")) {
                uncrossedItem = uncrossedItem.replace("<html><strike>", "").replace("</strike></html>", "");
                listModel.set(index, uncrossedItem);
                // Update the completion status in the database for the uncrossed item
                String sql = "UPDATE " + username + "_todo_list SET complete=0 WHERE item=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, uncrossedItem); // Update completion status for uncrossedItem
                    pstmt.executeUpdate();
                }
            }
        }
    }


    // Remove the user_id parameter from the DELETE SQL statement
    private void deleteItem(String item) throws SQLException {
        String sql = "DELETE FROM " + username + "_todo_list WHERE item=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item);
            pstmt.executeUpdate();
            DefaultListModel<String> listModel = (DefaultListModel<String>) list.getModel();
            // Remove the item from the list model
            for (int i = 0; i < listModel.getSize(); i++) {
                if (listModel.getElementAt(i).equals(item)) {
                    listModel.removeElementAt(i);
                    break;
                }
            }
        }
    }


    private void loadTasksForUser(String username) {
        try {
            // Create the necessary tables if they don't exist
            createTodoListTable(username);

            // Populate the list box with tasks
            populateListBox();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}