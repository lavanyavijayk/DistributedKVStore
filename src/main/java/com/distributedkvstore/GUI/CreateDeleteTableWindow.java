package com.distributedkvstore.GUI;

import javax.swing.*;

import com.distributedkvstore.client.ClientServerMain;
import com.distributedkvstore.helper.UserTableCRUDoperations;
import com.fasterxml.jackson.core.JsonProcessingException;


public class CreateDeleteTableWindow extends BaseTemplate {

    // Create and Delete table page.
    public CreateDeleteTableWindow(JFrame parent) {
        setTitle("Create/Delete Table");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);

        JLabel tableNameLabel = new JLabel("Table Name");
        JTextField tableNameField = new JTextField();
        JButton createTableButton = new JButton("Create Table");
        JButton deleteTableButton = new JButton("Delete Table");

        // Align the buttons
        tableNameLabel.setBounds(150, 200, 100, 30);
        tableNameField.setBounds(250, 200, 200, 30);
        createTableButton.setBounds(150, 300, 150, 30);
        deleteTableButton.setBounds(320, 300, 150, 30);
        JButton backButton = BackButton();

        add(tableNameLabel);
        add(tableNameField);
        add(createTableButton);
        add(deleteTableButton);
        add(backButton);

        // Back button navigates back to the parent page
        backButton.addActionListener(e -> {NavigationPage.navigateBack(parent, this);});

        // Create new table for thr user
        createTableButton.addActionListener(e -> {
            String tableName =  tableNameField.getText();
            try {
                if (UserTableCRUDoperations.tableExists(tableName)){
                    JOptionPane.showMessageDialog(this,
                            "Table already exists.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                } else {
                    if (!tableName.isEmpty()) {
                        System.out.println("Initiating user table creation");
                        try {
                            UserTableCRUDoperations.putOperation(tableName);
                            JOptionPane.showMessageDialog(this,
                                                  "Table created successfully!");
                        } catch (JsonProcessingException ex) {
                            throw new RuntimeException(ex);
                        }
                        NavigationPage.navigateBack(parent, this);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                             "Please fill in all fields!",
                                                 "Warning",
                                                      JOptionPane.WARNING_MESSAGE);
                    }
                    }
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Delete a table for the user
        deleteTableButton.addActionListener(e -> {
            String tableName =  tableNameField.getText();
            if (!tableName.isEmpty()) {
                System.out.println("Initiating user table creation");
                try {
                    UserTableCRUDoperations.deleteOperation(tableName);
                    JOptionPane.showMessageDialog(this,
                            "Table deleted successfully (if it existed)");
                    System.out.println("Logged in successfully");
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
                this.dispose();
                ClientServerMain.showNavigationPage();
            } else {
                JOptionPane.showMessageDialog(this,
                                     "Please fill in all fields!",
                                         "Warning",
                                              JOptionPane.WARNING_MESSAGE);
            }
        });

    }
}
