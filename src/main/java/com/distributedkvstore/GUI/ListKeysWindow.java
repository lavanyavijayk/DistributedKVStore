package com.distributedkvstore.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.distributedkvstore.helper.GlobalStoreCRUDoperations;
import com.fasterxml.jackson.core.JsonProcessingException;


public class ListKeysWindow extends BaseTemplate {

    private DefaultListModel<String> keysListModel;

    public ListKeysWindow(JFrame parent) {
        setTitle("List Keys");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);

        JLabel tableNameLabel = new JLabel("Table Name");
        JTextField tableNameField = new JTextField();
        JButton listKeysButton = new JButton("List Keys");
        JComboBox<String> tableNameDropdown = new JComboBox<>();
        populateTableDropdown(tableNameDropdown);
        SwingUtilities.invokeLater(() -> {
            System.out.println("Checking if dropdown has items...");
            if (tableNameDropdown.getItemCount() == 0) {
                System.out.println("No tables found. Navigating back...");
                NavigationPage.navigateBack(parent, this);
            }
        });


        tableNameLabel.setBounds(150, 200, 100, 30);
        tableNameDropdown.setBounds(250, 200, 200, 30);
        listKeysButton.setBounds(250, 250, 100, 30);
        JButton backButton = BackButton();

        keysListModel = new DefaultListModel<>();
        JList<String> keysList = new JList<>(keysListModel);
        JScrollPane keysScrollPane = new JScrollPane(keysList);
        keysScrollPane.setBounds(150, 300, 300, 130);

        add(tableNameLabel);
        add(tableNameDropdown);
        add(listKeysButton);
        add(backButton);
        add(keysScrollPane);

        listKeysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = (String) tableNameDropdown.getSelectedItem();
                if (!tableName.isEmpty()) {
                    List<String> keys = null;
                    try {
                        keys = fetchKeys(tableName);
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                    updateKeysList(keys);
                } else {
                    JOptionPane.showMessageDialog(
                            ListKeysWindow.this,
                            "Please enter a table name.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        backButton.addActionListener(e -> {NavigationPage.navigateBack(parent, this);});
    }

    private List<String> fetchKeys(String tableName) throws JsonProcessingException {
        String keyPrefix = tableName +"_";
        return GlobalStoreCRUDoperations.listKeysOperation(keyPrefix);
    }

    private void updateKeysList(List<String> keys) {
        keysListModel.clear();
        for (String key : keys) {
            keysListModel.addElement(key);
        }
    }
}