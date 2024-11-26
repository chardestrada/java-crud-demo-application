package com.example.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CustomerCRUD extends JFrame {
    // GUI Components
    private JTextField txtFirstName, txtEmail;
    private JPasswordField txtPassword;
    private JTextArea txtOutput;
    private JButton btnAdd, btnUpdate, btnDelete, btnView, btnSearch;
    private JTextField txtCustomerId;

    public CustomerCRUD() {
        setTitle("Customer CRUD Application");
        setLayout(new BorderLayout());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create input panel for customer details
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        inputPanel.add(new JLabel("Customer ID:"));
        txtCustomerId = new JTextField();
        txtCustomerId.setEditable(false);  // Auto-generated, no need to enter manually
        inputPanel.add(txtCustomerId);

        inputPanel.add(new JLabel("First Name:"));
        txtFirstName = new JTextField();
        inputPanel.add(txtFirstName);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        inputPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        inputPanel.add(txtPassword);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnView = new JButton("View All");
        btnSearch = new JButton("Search by Customer ID");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnView);
        buttonPanel.add(btnSearch);

        // Output Area for displaying records
        txtOutput = new JTextArea(10, 40);
        txtOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtOutput);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Add Action Listeners
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCustomers();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchCustomerById();
            }
        });
    }

    private void searchCustomerById() {
        String customerIdText = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        if (customerIdText != null && !customerIdText.isEmpty()) {
            try {
                int customerId = Integer.parseInt(customerIdText);

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "SELECT * FROM customer WHERE customer_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, customerId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        String firstName = rs.getString("firstname");
                        String email = rs.getString("email");
                        String password = rs.getString("password");

                        txtCustomerId.setText(String.valueOf(customerId));
                        txtFirstName.setText(firstName);
                        txtEmail.setText(email);
                        txtPassword.setText(password);
                    } else {
                        JOptionPane.showMessageDialog(this, "Customer with this ID not found.");
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid Customer ID format.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error retrieving customer.");
            }
        }
    }

    private void addCustomer() {
        String firstName = txtFirstName.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());

        if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO customer (firstname, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, firstName);
            stmt.setString(2, email);
            stmt.setString(3, password);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer added successfully!");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding customer.");
        }
    }

    private void updateCustomer() {
        String customerIdText = txtCustomerId.getText();
        if (customerIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer ID cannot be empty!");
            return;
        }

        int customerId = Integer.parseInt(customerIdText);
        String firstName = txtFirstName.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());

        if (firstName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE customer SET firstname = ?, email = ?, password = ? WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, firstName);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setInt(4, customerId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found.");
            }

            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer.");
        }
    }

    private void deleteCustomer() {
        String customerIdText = txtCustomerId.getText();
        if (customerIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer ID cannot be empty!");
            return;
        }

        int customerId = Integer.parseInt(customerIdText);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM customer WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, customerId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting customer.");
        }
    }

    private void viewCustomers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM customer";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            txtOutput.setText("");
            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String firstName = rs.getString("firstname");
                String email = rs.getString("email");
                String password = rs.getString("password");

                txtOutput.append("ID: " + customerId + ", Name: " + firstName + ", Email: " + email + ", Password: " + password + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving customers.");
        }
    }

    private void clearFields() {
        txtCustomerId.setText("");
        txtFirstName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CustomerCRUD().setVisible(true);
            }
        });
    }
}
