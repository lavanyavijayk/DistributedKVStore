package com.distributedkvstore.GUI;

import com.distributedkvstore.helper.UserTableCRUDoperations;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BaseTemplate extends JFrame {

    private static javax.swing.JButton JButton;
    protected JPanel contentPanel; // Panel for child-specific components

    public BaseTemplate() {

    }

    // Method to initiate the base template with the header.
    protected static JPanel initializeBaseGUI() {
        // Create a header panel (black strip with text)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.BLACK);
        headerPanel.setBounds(0, 0, 600, 50); // Full width, fixed height
        headerPanel.setLayout(null);

        // Add title to the header panel
        JLabel titleLabel = new JLabel("Key-Value Store");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Custom font
        titleLabel.setBounds(200, 10, 300, 30); // Position the text
        headerPanel.add(titleLabel);

        // Add the header panel to the JFrame
        return headerPanel;
    }

    // Method to initiate the back button.
    protected static JButton BackButton(){
        JButton backButton = new JButton("Back");
        backButton.setBounds(450, 70, 70, 30);
        return backButton;
    }

    // Method to list all the user tables.
    protected static List<String> listUserTables() throws JsonProcessingException {
        return UserTableCRUDoperations.getOperation();
    }

    // Method to populate the user table into to the dropdown
    protected void populateTableDropdown(JComboBox<String> tableNameDropdown) {
        try {
            List<String> userTables = listUserTables();
            for (String table : userTables) {
                tableNameDropdown.addItem(table);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please create a table to continue.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
