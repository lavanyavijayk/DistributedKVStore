package com.distributedkvstore.GUI;

import javax.swing.*;


public class NavigationPage extends BaseTemplate {
    private Runnable logoutAction;

    public NavigationPage() {
        setTitle("Navigation Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);

        JButton createDeleteTableButton = new JButton("Create/Delete Table");
        JButton listTablesButton = new JButton("List Tables");
        JButton createUpdateKeysButton = new JButton("Create/Update Keys");
        JButton listKeysButton = new JButton("List Keys");
        JButton deleteKeysButton = new JButton("Delete Keys");
        JButton getKeysButton = new JButton("Get Keys");
        JButton logoutButton = new JButton("Logout");

        createDeleteTableButton.setBounds(200, 75, 200, 30);
        listTablesButton.setBounds(200, 150, 200, 30);
        createUpdateKeysButton.setBounds(200, 225, 200, 30);
        listKeysButton.setBounds(200, 300, 200, 30);
        deleteKeysButton.setBounds(200, 375, 200, 30);
        getKeysButton.setBounds(200, 450, 200, 30);
        logoutButton.setBounds(200, 525, 200, 30);

        add(createDeleteTableButton);
        add(listTablesButton);
        add(createUpdateKeysButton);
        add(listKeysButton);
        add(deleteKeysButton);
        add(getKeysButton);
        add(logoutButton);

        createDeleteTableButton.addActionListener(e -> openCreateDeleteTablePage());
        logoutButton.addActionListener(e -> {
            if (logoutAction != null) {
                logoutAction.run();
            }
        });
        createUpdateKeysButton.addActionListener(e -> openCreateUpdateKeysPage());
        listKeysButton.addActionListener(e -> openListKeysPage());
        listTablesButton.addActionListener(e -> openListTablesPage());
        deleteKeysButton.addActionListener(e -> openDeleteKeysPage());
        getKeysButton.addActionListener(e -> openGetKeyPage());
    }

    private void openCreateDeleteTablePage() {
        CreateDeleteTableWindow createDeleteTableWindow = new CreateDeleteTableWindow(this);
        createDeleteTableWindow.setVisible(true);
        this.setVisible(false);
    }

    private void openCreateUpdateKeysPage() {
        CreateUpdateKeysWindow createUpdateKeysWindow = new CreateUpdateKeysWindow(this);
        createUpdateKeysWindow.setVisible(true);
        this.setVisible(false);
    }

    private void openDeleteKeysPage(){
        DeleteKeysWindow deleteKeysWindow = new DeleteKeysWindow(this);
        deleteKeysWindow.setVisible(true);
        this.setVisible(false);
    }

    private void openListKeysPage(){
        ListKeysWindow listKeysWindow = new ListKeysWindow(this);
        listKeysWindow.setVisible(true);
        this.setVisible(false);
    }

    private void openListTablesPage(){
        ListTablesWindow listTablesWindow = new ListTablesWindow(this);
        listTablesWindow.setVisible(true);
        this.setVisible(false);
    }

    private void openGetKeyPage(){
        GetValueWindow getKeyWindow = new GetValueWindow(this);
        getKeyWindow.setVisible(true);
        this.setVisible(false);
    }

    public void setLogoutAction(Runnable logoutAction) {
        this.logoutAction = () -> {
            if (logoutAction != null) {
                logoutAction.run();
            }

        };
    }

    // Method to navigate back to parent page from current page.
    protected static void navigateBack(JFrame parent, JFrame currentFrame) {
        parent.setVisible(true);
        currentFrame.dispose();
    }
}

