package com.library.ui;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.LibraryService;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

//admin interface ui
public class AdminClass extends JFrame {
    private final User admin;

    //admin ui after logging in
    public AdminClass(User admin) {
        super("Admin Interface");
        this.admin = admin;
        //close application when closing admin ui
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //layout to center panel
        setLayout(new BorderLayout());

        //Create panel with 3 rows, 2 columns, 20px gaps
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        //init buttons
        JButton btnSearch   = new JButton("Search Books");
        JButton btnAddBook  = new JButton("Add Book");
        JButton btnView     = new JButton("Book List");
        JButton btnAddUser  = new JButton("Add User");
        JButton btnDelUser  = new JButton("Delete User");
        JButton btnLogout   = new JButton("Log Out");
        
        //add to panel
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnAddBook);
        buttonPanel.add(btnView);
        buttonPanel.add(btnAddUser);
        buttonPanel.add(btnDelUser);
        buttonPanel.add(btnLogout);

        add(buttonPanel, BorderLayout.CENTER); //center the layout

        //open button listeners

        //for searching books
        btnSearch.addActionListener(e -> {
            String kw = JOptionPane.showInputDialog(this, "Enter keyword:");
            if (kw != null && !kw.isBlank()) {
                try {
                    List<Book> books = LibraryService.searchBooks(kw);
                    JTextArea area = new JTextArea(10, 40);
                    books.forEach(b -> area.append(b + "\n"));
                    JOptionPane.showMessageDialog(this,
                        new JScrollPane(area),
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    showError(ex);
                }
            }
        });
        
        //for adding books
        btnAddBook.addActionListener(e -> {
            JTextField fIsbn   = new JTextField(10);
            JTextField fTitle  = new JTextField(20);
            JTextField fAuthor = new JTextField(20);
            JPanel panel = new JPanel(new GridLayout(0,1,5,5));
            panel.add(new JLabel("ISBN:"));   panel.add(fIsbn);
            panel.add(new JLabel("Title:"));  panel.add(fTitle);
            panel.add(new JLabel("Author:")); panel.add(fAuthor);
            if (JOptionPane.showConfirmDialog(this, panel, "Add Book",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    boolean ok = LibraryService.addBook(
                      new Book(fIsbn.getText().trim(),
                               fTitle.getText().trim(),
                               fAuthor.getText().trim(),
                               true));
                    JOptionPane.showMessageDialog(this,
                        ok ? "Book added." : "Failed to add book.",
                        "Result",
                        ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    showError(ex);
                }
            }
        });

        //for viewing book list
        btnView.addActionListener(e -> {
            try {
                List<Book> books = LibraryService.listBooks();
                JTextArea area = new JTextArea(10, 40);
                books.forEach(b -> area.append(b + "\n"));
                JOptionPane.showMessageDialog(this,
                    new JScrollPane(area),
                    "All Books",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                showError(ex);
            }
        });

        //for adding user
        btnAddUser.addActionListener(e -> {
            JTextField fUser = new JTextField(10);
            JPasswordField fPass = new JPasswordField(10);
            JCheckBox chkAdmin = new JCheckBox("Is Admin?");
            JPanel panel = new JPanel(new GridLayout(0,1,5,5));
            panel.add(new JLabel("Username:")); panel.add(fUser);
            panel.add(new JLabel("Password:")); panel.add(fPass);
            panel.add(chkAdmin);
            if (JOptionPane.showConfirmDialog(this, panel, "Add User",
                    JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                try {
                    boolean ok = AuthService.registerUser(
                      fUser.getText().trim(),
                      new String(fPass.getPassword()),
                      chkAdmin.isSelected());
                    JOptionPane.showMessageDialog(this,
                        ok ? "User added." : "Failed to add user.",
                        "Result",
                        ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        //for deleting user
        btnDelUser.addActionListener(e -> {
            String user = JOptionPane.showInputDialog(this, "Username to delete:");
            if (user != null && !user.isBlank()) {
                try {
                    boolean ok = AuthService.deleteUser(user.trim());
                    JOptionPane.showMessageDialog(this,
                        ok ? "User deleted." : "User not found.",
                        "Result",
                        ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    showError(ex);
                }
            }
        });

        //logout button
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        pack();
        setLocationRelativeTo(null);
    }

    //error dialog for exception handling
    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error:\n" + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
