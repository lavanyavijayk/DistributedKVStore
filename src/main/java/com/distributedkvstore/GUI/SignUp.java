package com.distributedkvstore.GUI;

import javax.swing.*;

import com.distributedkvstore.client.ClientServerMain;
import com.distributedkvstore.client.KeyValueClient;
import com.distributedkvstore.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class SignUp extends BaseTemplate{

    public SignUp() {
        setTitle("Login / Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(null);
        JPanel headerPanel = initializeBaseGUI();

        add(headerPanel);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(150, 200, 100, 30);
        add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(250, 200, 200, 30);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(150, 250, 100, 30);
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(250, 250, 200, 30);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(300, 300, 90, 30);
        add(loginButton);

        JButton signupButton = new JButton("Sign-Up");
        signupButton.setBounds(200, 300, 90, 30);
        add(signupButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (!username.isEmpty() && !password.isEmpty()) {
                System.out.println("Initiating the Login Process");
                try {
                    User user = User.authenticate(username,password);
                    if (user==null){
                        JOptionPane.showMessageDialog(this,
                                              "Wrong Credentials.",
                                                  "Warning",
                                                       JOptionPane.WARNING_MESSAGE);
                    } else {
                        KeyValueClient.getInstance().setUserSession(user);
                        ClientServerMain.showNavigationPage();
                    }
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                } catch (InvalidKeySpecException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                                     "Please fill in all fields!",
                                         "Warning",
                                              JOptionPane.WARNING_MESSAGE);
            }
        });

        signupButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (!username.isEmpty() && !password.isEmpty()) {
                User newUser = null;
                try {
                    User user = User.getUserObject(username);
                    if (user!=null){
                        JOptionPane.showMessageDialog(this,
                                             "An Account with the same username already exists",
                                                 "Warning",
                                                      JOptionPane.WARNING_MESSAGE);
                    } else {
                        newUser = User.SignUpUser(username, password);
                        KeyValueClient.getInstance().setUserSession(newUser);
                        ClientServerMain.showNavigationPage();
                    }
                } catch (JsonProcessingException ex) {
                    throw new RuntimeException(ex);
                } catch (NoSuchAlgorithmException ex) {
                    throw new RuntimeException(ex);
                } catch (InvalidKeySpecException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                                     "Please fill in all fields!",
                                         "Warning",
                                              JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public JFrame getFrame() {
        return this;
    }
}
