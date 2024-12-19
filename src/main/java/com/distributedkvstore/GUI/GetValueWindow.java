package com.distributedkvstore.GUI;

import javax.swing.*;
import java.awt.*;

import com.distributedkvstore.helper.GlobalStoreCRUDoperations;
import com.fasterxml.jackson.core.JsonProcessingException;


public class GetValueWindow extends BaseTemplate {

    private final JTextField keyField;
    private final JLabel valueLabel;
    private final JComboBox<String> tableNameDropdown = new JComboBox<>();

    public GetValueWindow(JFrame parent) {

        setTitle("Key-Value Fetcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);


        JLabel tableNameLabel = new JLabel("Table Name:");
        populateTableDropdown(tableNameDropdown);

        tableNameLabel.setBounds(150, 200, 100, 30);
        tableNameDropdown.setBounds(250, 200, 200, 30);

        add(tableNameLabel);
        add(tableNameDropdown);

        SwingUtilities.invokeLater(() -> {
            System.out.println("Checking if dropdown has items...");
            if (tableNameDropdown.getItemCount() == 0) {
                System.out.println("No tables found. Navigating back...");
                NavigationPage.navigateBack(parent, this);
            }
        });

        // Key Components
        JLabel keyLabel = new JLabel("Key:");
        keyField = new JTextField();

        keyLabel.setBounds(150, 250, 100, 30);
        keyField.setBounds(250, 250, 200, 30);

        add(keyLabel);
        add(keyField);

        // Get Value Button
        JButton getValueButton = new JButton("Get Value");
        getValueButton.setBounds(250, 300, 100, 30);
        add(getValueButton);

        // Value Display Label
        valueLabel = new JLabel("Value: ");
        valueLabel.setBounds(150, 350, 300, 30);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(valueLabel);

        // Back Button
        JButton backButton = BackButton();
        add(backButton);

        // Action Listeners
        backButton.addActionListener(e -> {NavigationPage.navigateBack(parent, this);});
        getValueButton.addActionListener(e -> fetchAndDisplayValue());

        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private void fetchAndDisplayValue() {
        String tableName = (String) tableNameDropdown.getSelectedItem();
        String key = keyField.getText().trim();

        if (tableName.isEmpty() || key.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                                  "Please fill in both Table Name and Key!",
                                      "Warning",
                                           JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simulate fetching value from a backend or database
        String value = fetchValueFromStore(tableName, key);

        if (value != null) {
            valueLabel.setText("Value: " + value);
        } else {
            valueLabel.setText("Value: Key not found.");
        }
    }

    // Method to fetch data from the data store
    private String fetchValueFromStore(String tableName, String key) {
        String keyString = tableName + "_" + key;
        try {
            return GlobalStoreCRUDoperations.getOperation(keyString);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
