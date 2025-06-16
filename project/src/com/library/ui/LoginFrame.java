package com.library.ui;

import com.library.dao.DatabaseManager;
import com.library.model.User;    // ← import AuthService
import com.library.service.AuthService;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.swing.*;

//login window
public class LoginFrame extends JFrame {
    private final JTextField    userField = new JTextField(15);
    private final JPasswordField passField = new JPasswordField(15);

    //construct login panel
    public LoginFrame() {
        super("Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        //username and password input
        JPanel center = new JPanel(new GridLayout(2,2,5,5));
        center.add(new JLabel("Username:", SwingConstants.RIGHT));
        center.add(userField);
        center.add(new JLabel("Password:", SwingConstants.RIGHT));
        center.add(passField);
        add(center, BorderLayout.CENTER);

        //buttons for login and register
        JButton loginBtn    = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JPanel south = new JPanel();
        south.add(loginBtn);
        south.add(registerBtn);
        add(south, BorderLayout.SOUTH);

        //button listener
        loginBtn.addActionListener(e -> attemptLogin());
        registerBtn.addActionListener(e -> showRegisterPanel());

        pack();
        setLocationRelativeTo(null);
    }

    //attempt to authenticate with username and password, if success, opens respective panel for admin or member
    private void attemptLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        try {
            //delegate authentication to authservice.java
            User u = AuthService.authenticate(user, pass);
            if (u == null) {
                JOptionPane.showMessageDialog(this,
                  "Invalid credentials.",
                  "Try Again",
                  JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Open interface by user type and close login window
            dispose();
            if (u.isAdmin()) {
                new AdminClass(u).setVisible(true);
            } else {
                new UserClass(u).setVisible(true);
            }

        } catch (SQLException | NoSuchAlgorithmException ex) {
            //db hashing error(sqlexception, nsaexception)
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
              "Error during login:\n" + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
    }

    //Registration page with username password and admin/member
    //calls authservice
    private void showRegisterPanel() {
        JTextField    newUser = new JTextField(10);
        JPasswordField newPass = new JPasswordField(10);
        JCheckBox     isAdmin = new JCheckBox("Admin?");

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("New Username:"));
        panel.add(newUser);
        panel.add(new JLabel("New Password:"));
        panel.add(newPass);
        panel.add(isAdmin);

        int result = JOptionPane.showConfirmDialog(
          this, panel, "Register", JOptionPane.OK_CANCEL_OPTION,
          JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                //call authservice
                boolean ok = AuthService.registerUser(
                  newUser.getText().trim(),
                  new String(newPass.getPassword()),
                  isAdmin.isSelected()
                );
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                      "Registration successful!",
                      "Success!",
                      JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                      "Username is taken.",
                      "Error!",
                      JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException | NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                  "Error during registration:\n" + ex.getMessage(),
                  "Error!",
                  JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseManager.connect();
                new LoginFrame().setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                  "Service not working\n" + ex.getMessage(),
                  "Fatal Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
