package com.library.ui;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.LibraryService;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

//Member interace ui
public class UserClass extends JFrame {
    private final User user;

    //construct ui
    public UserClass(User user) {
        super("Member Interface");
        this.user = user;
        //close application when window is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //layout
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        //init buttons
        JButton btnSearch = new JButton("Search Books");
        JButton btnBorrow = new JButton("Borrow Book");
        JButton btnReturn = new JButton("Return Book");
        JButton btnPay    = new JButton("Pay Fine");
        JButton btnView   = new JButton("Book List");
        JButton btnLogout = new JButton("Log Out");
        //add buttons
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnBorrow);
        buttonPanel.add(btnReturn);
        buttonPanel.add(btnPay);
        buttonPanel.add(btnView);
        buttonPanel.add(btnLogout);

        add(buttonPanel, BorderLayout.CENTER);

        //lilsteners for all buttons
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

        btnBorrow.addActionListener(e -> {
            String isbn = JOptionPane.showInputDialog(this, "ISBN to borrow:");
            if (isbn != null && !isbn.isBlank()) {
                try {
                    LocalDate due = LibraryService.borrowBook(user.getId(), isbn.trim());
                    if (due == null) {
                        JOptionPane.showMessageDialog(this,
                          "Cannot borrow (unavailable or not found).",
                          "Borrow Failed",
                          JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                          "Borrowed! Due date: " + due,
                          "Success",
                          JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    showError(ex);
                }
            }
        });

        btnReturn.addActionListener(e -> {
            String isbn = JOptionPane.showInputDialog(this, "ISBN to return:");
            if (isbn != null && !isbn.isBlank()) {
                try {
                    double fine = LibraryService.returnBook(user.getId(), isbn.trim());
                    if (fine < 0) {
                        JOptionPane.showMessageDialog(this,
                          "No active loan for that ISBN.",
                          "Return Failed",
                          JOptionPane.ERROR_MESSAGE);
                    } else if (fine == 0.0) {
                        JOptionPane.showMessageDialog(this,
                          "Returned on time. No fine owed.",
                          "Return Success",
                          JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                          String.format("Returned late! Fine: $%.2f", fine),
                          "Return Success",
                          JOptionPane.WARNING_MESSAGE);
                    }
                } catch (SQLException ex) {
                    showError(ex);
                }
            }
        });

        btnPay.addActionListener(e -> {
            try {
                double owed = LibraryService.getOutstandingFines(user.getId());
                if (owed <= 0) {
                    JOptionPane.showMessageDialog(this,
                      "You have no outstanding fines.",
                      "No Fines",
                      JOptionPane.INFORMATION_MESSAGE);
                } else {
                    int choice = JOptionPane.showConfirmDialog(this,
                      String.format("You owe $%.2f. Pay now?", owed),
                      "Pay Fine",
                      JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        double paid = LibraryService.payFines(user.getId());
                        JOptionPane.showMessageDialog(this,
                          String.format("Paid $%.2f. Thank you!", paid),
                          "Fine Paid",
                          JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                showError(ex);
            }
        });

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

        btnLogout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
          "Error:\n" + ex.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
}
