package com.distributedkvstore.GUI;

import com.distributedkvstore.client.ClientServerMain;
import com.distributedkvstore.helper.GlobalStoreCRUDoperations;
import com.fasterxml.jackson.core.JsonProcessingException;


import javax.swing.*;
import java.util.List;

public class CreateUpdateKeysWindow extends BaseTemplate {

    // Create and update key page GUI
    public CreateUpdateKeysWindow(JFrame parent) {
        super();
        setTitle("Create/Update Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);

        JLabel tableNameLabel = new JLabel("Table Name");
        JComboBox<String> tableNameDropdown = new JComboBox<>();
        populateTableDropdown(tableNameDropdown);

        JLabel keyLabel = new JLabel("Key");
        JLabel valueLabel = new JLabel("Value");
        JTextField keyField = new JTextField();
        JTextField valueField = new JTextField();
        JButton createKeyButton = new JButton("Create Key");
        JButton updateKeyButton = new JButton("Update Key");

        tableNameLabel.setBounds(100, 200, 100, 30);
        tableNameDropdown.setBounds(200, 200, 200, 30);
        keyLabel.setBounds(100, 250, 300, 30);
        keyField.setBounds(200, 250, 300, 30);
        valueLabel.setBounds(100, 300, 300, 30);
        valueField.setBounds(200, 300, 300, 30);
        createKeyButton.setBounds(100, 400, 150, 30);
        updateKeyButton.setBounds(300, 400, 150, 30);
        JButton backButton = BackButton();

        add(tableNameLabel);
        add(tableNameDropdown);
        add(keyLabel);
        add(keyField);
        add(valueLabel);
        add(valueField);
        add(createKeyButton);
        add(updateKeyButton);
        add(backButton);

        SwingUtilities.invokeLater(() -> {
            System.out.println("Checking if dropdown has items...");
            if (tableNameDropdown.getItemCount() == 0) {
                System.out.println("No tables found. Navigating back...");
                NavigationPage.navigateBack(parent, this);
            }
        });

        backButton.addActionListener(e -> {NavigationPage.navigateBack(parent, this);});
        createKeyButton.addActionListener(e -> {
            String tableName = (String) tableNameDropdown.getSelectedItem();
            String keyName = keyField.getText();
            String value = valueField.getText();
            if (tableName != null && !keyName.isEmpty() && !value.isEmpty()) {
                System.out.println("Initiating user table key-value creation");
                try {

                    String keyString = tableName + "_" + keyName;
                    //Checking is the key exists
                    String response = GlobalStoreCRUDoperations.getOperation(keyString);
                    if (response.isEmpty()){
                        GlobalStoreCRUDoperations.postOperation(keyString, value);
                        JOptionPane.showMessageDialog(this, "Key created successfully!");
                        this.dispose();
                        ClientServerMain.showNavigationPage();
                    } else {
                        JOptionPane.showMessageDialog(this, "Key already exists!");
                    }
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                                     "Please fill in all fields!",
                                         "Warning",
                                              JOptionPane.WARNING_MESSAGE);
            }
        });

        updateKeyButton.addActionListener(e -> {
            String tableName = (String) tableNameDropdown.getSelectedItem();
            String keyName = keyField.getText();
            String value = valueField.getText();
            if (tableName != null && !keyName.isEmpty() && !value.isEmpty()) {
                System.out.println("Initiating user table key-value update");
                try {
                    String keyString = tableName + "_" + keyName;
                    GlobalStoreCRUDoperations.putOperation(keyString, value);
                    JOptionPane.showMessageDialog(this,
                            "Key updated successfully(If exists)! or Created successfully!");

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
