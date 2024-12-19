package com.distributedkvstore.client;

import javax.swing.*;

import com.beust.jcommander.JCommander;
import com.distributedkvstore.GUI.NavigationPage;
import com.distributedkvstore.GUI.SignUp;


public class  ClientServerMain {
    private static JFrame currentFrame;
    private static KeyValueClient client = KeyValueClient.getInstance();

    // Method to Initiate and run the client server
    public static void main(String[] args) {
        try {
            JCommander.newBuilder()
                    .addObject(client)
                    .build()
                    .parse(args);
            client.run();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Start the application with the SignUp window
        SwingUtilities.invokeLater(() -> showSignUpPage());

    }

    //Method to open up the signup window
    public static void showSignUpPage() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        SignUp signUp = new SignUp();
        currentFrame = signUp.getFrame();
        currentFrame.setVisible(true);
    }

    //Method to open up the navigation window
    public static void showNavigationPage() {
        if (currentFrame != null) {
            currentFrame.dispose();
        }
        NavigationPage navigationPage = new NavigationPage();
        navigationPage.setLogoutAction( ClientServerMain::showSignUpPage);
        currentFrame = navigationPage;
        currentFrame.setVisible(true);
    }
}