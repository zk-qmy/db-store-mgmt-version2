package register.login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton btnLogin = new JButton("Login");
    //private JButton btnRegister = new JButton("Create Account");
    public JPasswordField getPasswordField(){
        return passwordField;
    }
    public JTextField getUsernameField() {
        return usernameField;
    }

    public LoginView() {
        setTitle("Login");
        setSize(400, 300);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // Prevent resizing

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // Light background color

        // Set constraints for components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add space between components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the west (left)

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome to the App!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(welcomeLabel, gbc);

        // Username Label and Field
        gbc.gridwidth = 1; // Reset to default
        JLabel usernameLabel = new JLabel("Username: ");
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; // Move to the right
        mainPanel.add(usernameField, gbc);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password: ");
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Button Panel for Login and Register
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        btnLogin.setBackground(new Color(51, 153, 255)); // Blue color
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        //btnRegister.setBackground(new Color(153, 51, 255)); // Purple color
        //btnRegister.setForeground(Color.WHITE);
        //btnRegister.setFocusPainted(false);
        buttonPanel.add(btnLogin);
        //buttonPanel.add(btnRegister);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; // Center the button panel
        mainPanel.add(buttonPanel, gbc);

        // Add main panel to frame
        add(mainPanel);
    }
    public JButton getBtnLogin() {
        return btnLogin;
    }
}
