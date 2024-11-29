package register.createaccount;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {
    // Register button
    private JButton btnRegister = new JButton("Register");
    //private JButton btnLogin = new JButton("Login");
    private JTextField nameField = new JTextField(20);
    private JTextField addressField = new JTextField(20);
    private JTextField phoneField = new JTextField(20);
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JPasswordField confirmPasswordField = new JPasswordField(20);
    public JTextField getNameField() {
        return nameField;
    }
    public JTextField getAddressField() {
        return addressField;
    }
    public JTextField getPhoneField() {
        return phoneField;
    }
    public JTextField getUsernameField() {
        return usernameField;
    }
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    public JPasswordField getConfirmPasswordField() {
        return confirmPasswordField;
    }

    public RegisterView() {
        setTitle("Register");
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Create main panel with BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        // Name field
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel nameLabel = new JLabel("Name: ");
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        // Address field
        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel addressLabel = new JLabel("Address: ");
        addressPanel.add(addressLabel);
        addressPanel.add(addressField);

        // Phone field
        JPanel phonePanel = new JPanel();
        phonePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel phoneLabel = new JLabel("Phone Number: ");
        phonePanel.add(phoneLabel);
        phonePanel.add(phoneField);

        // Username field
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Username: ");

        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // Password field
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel passwordLabel = new JLabel("Password: ");
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // Confirm Password field
        JPanel confirmPasswordPanel = new JPanel();
        confirmPasswordPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel confirmPasswordLabel = new JLabel("Confirm Password: ");
        confirmPasswordPanel.add(confirmPasswordLabel);
        confirmPasswordPanel.add(confirmPasswordField);


        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to main panel
        mainPanel.add(namePanel);
        mainPanel.add(addressPanel);
        mainPanel.add(phonePanel);
        mainPanel.add(usernamePanel);
        mainPanel.add(passwordPanel);
        mainPanel.add(confirmPasswordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        mainPanel.add(btnRegister);

        // Add main panel to frame
        add(mainPanel);
    }
    public JButton getBtnRegister() {
        return btnRegister;
    }
}
