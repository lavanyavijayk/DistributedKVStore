package com.distributedkvstore.GUI;

import javax.swing.*;

import com.distributedkvstore.helper.GlobalStoreCRUDoperations;
import com.fasterxml.jackson.core.JsonProcessingException;


public class DeleteKeysWindow extends BaseTemplate {

    public DeleteKeysWindow(JFrame parent) {
        setTitle("Delete Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);

        JLabel tableNameLabel = new JLabel("Table Name");
        JTextField tableNameField = new JTextField();
        JLabel keyLabel = new JLabel("Key");
        JTextField keyField = new JTextField();
        JButton deleteKeyButton = new JButton("Delete Key");
        JComboBox<String> tableNameDropdown = new JComboBox<>();
        populateTableDropdown(tableNameDropdown);

        tableNameLabel.setBounds(100, 200, 100, 30);
        tableNameDropdown.setBounds(200, 200, 200, 30);
        keyLabel.setBounds(100, 250, 100, 30);
        keyField.setBounds(200, 250, 200, 30);
        deleteKeyButton.setBounds(150, 350, 200, 30);
        JButton backButton = BackButton();

        add(tableNameLabel);
        add(tableNameDropdown);
        add(keyLabel);
        add(keyField);
        add(deleteKeyButton);
        add(backButton);

        SwingUtilities.invokeLater(() -> {
            System.out.println("Checking if dropdown has items...");
            if (tableNameDropdown.getItemCount() == 0) {
                System.out.println("No tables found. Navigating back...");
                NavigationPage.navigateBack(parent, this);
            }
        });

        backButton.addActionListener(e -> {NavigationPage.navigateBack(parent, this);});
        deleteKeyButton.addActionListener(e -> {
            String tableName =  (String) tableNameDropdown.getSelectedItem();
            String keyName = keyField.getText();
            if (!tableName.isEmpty() && !keyName.isEmpty()) {
                System.out.println("Initiating user table key-value creation");
                try {
                    String keyString = tableName + "_" + keyName;
                    GlobalStoreCRUDoperations.deleteOperation(keyString);
                    JOptionPane.showMessageDialog(this, "Key Deleted successfully(If exists)!");

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
        });
    }
}
