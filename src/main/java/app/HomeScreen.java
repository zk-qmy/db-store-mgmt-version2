package app;

import register.createaccount.RegisterView;
import register.login.LoginView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreen extends JFrame {
    private JButton btnCreateAccount = new JButton("Create Account");
    private JButton btnLogin = new JButton("Login");
    private JLabel welcomeLabel = new JLabel("Welcome to the Application");
    public JButton getBtnLogin() {
        return btnLogin;
    }
    public JButton getBtnCreateAccount() {
        return btnCreateAccount;
    }

    public HomeScreen() {
        setTitle("Home Screen");
        setSize(1020, 800);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with BoxLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Set up the welcome label
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Set button alignment
        btnCreateAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to the main panel
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space
        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space
        mainPanel.add(btnCreateAccount);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space
        mainPanel.add(btnLogin);

        // Add main panel to the frame
        add(mainPanel);
    }
}
