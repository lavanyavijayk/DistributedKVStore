package com.distributedkvstore.GUI;

import javax.swing.*;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ListTablesWindow extends BaseTemplate {

    public ListTablesWindow(JFrame parent) {
        setTitle("List Tables");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);

        JLabel tablesLabel = new JLabel("Tables");
        JTextArea tablesArea = new JTextArea();
        tablesArea.setEditable(false); // Make the text area read-only

        tablesLabel.setBounds(100, 200, 100, 30);
        tablesArea.setBounds(200, 200, 200, 150);
        JButton backButton = BackButton();

        add(tablesLabel);
        add(tablesArea);
        add(backButton);

        // Populate the JTextArea with table names
        List<String> tables = null;
        try {
            tables = listUserTables();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (tables != null && !tables.isEmpty()) {
            StringBuilder tableNames = new StringBuilder();
            for (String table : tables) {
                tableNames.append(table).append("\n");
            }
            tablesArea.setText(tableNames.toString());
        } else {
            tablesArea.setText("No tables available.");
        }

        backButton.addActionListener(e -> {NavigationPage.navigateBack(parent,
                this);});
    }

}
