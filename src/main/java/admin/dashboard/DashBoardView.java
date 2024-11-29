package admin.dashboard;

import app.App;
import register.Session;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Map;

public class DashBoardView extends JFrame {
    private JPanel mainPanel, sidebarPanel, contentPanel, productInfoPanel, userInfoPanel, saleInfoPanel;
    private JButton btnManageUsers, btnManageOrders, btnRefresh, btnLogout, btnManageProducts;
    private JLabel dashboardTitle, saleLabel;

    public DashBoardView() {
        // Frame settings
        this.setTitle("Admin Dashboard");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // Main Panel
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // Sidebar Panel
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.LIGHT_GRAY);

        btnManageUsers = new JButton("Manage Users");
        btnManageOrders = new JButton("Manage Orders");
        btnManageProducts = new JButton("Manage Products");
        btnRefresh = new JButton("Refresh");
        btnLogout = new JButton("Logout");

        sidebarPanel.add(btnManageProducts);
        sidebarPanel.add(btnManageUsers);
        sidebarPanel.add(btnManageOrders);
        sidebarPanel.add(btnRefresh);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);

        // Content panel
        contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components

        // Info panels
        productInfoPanel = new JPanel();
        userInfoPanel = new JPanel();
        saleInfoPanel = new JPanel();

        // Set layout and borders for info panels
        productInfoPanel.setLayout(new BoxLayout(productInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        saleInfoPanel.setLayout(new BoxLayout(saleInfoPanel, BoxLayout.X_AXIS));

        // init borders
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, paddingBorder);

        // Initialize sales label
        saleLabel = new JLabel("Total Sales: 0.00", SwingConstants.CENTER);
        saleInfoPanel.add(saleLabel);

        // Add components to content panel using GridBagLayout
        gbc.weightx = 1.0; // Weight for horizontal resizing
        gbc.weighty = 0.1; // Weight for vertical resizing

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.1; // Less height for title
        dashboardTitle = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        dashboardTitle.setFont(new Font("Arial", Font.BOLD, 30));
        dashboardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(dashboardTitle, gbc);

        // Product Info Panel (Left)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.4; // More height for product info
        productInfoPanel.setBorder(compoundBorder);
        contentPanel.add(productInfoPanel, gbc);

        // User Info Panel (Right)
        gbc.gridx = 1; // Move to the right column
        gbc.gridy = 1; // Same row as Product Info
        userInfoPanel.setBorder(compoundBorder);
        contentPanel.add(userInfoPanel, gbc);

        // Sales Info Panel (Bottom)
        gbc.gridx = 0; // Reset to the left column
        gbc.gridy = 2; // Move to the next row for Sales Info
        gbc.gridwidth = 2; // Span both columns
        gbc.weighty = 0.1; // Less height for sales info
        saleInfoPanel.setBorder(compoundBorder);
        contentPanel.add(saleInfoPanel, gbc);



        // Add Sidebar and Content to Main Panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Button Actions
        btnManageProducts.addActionListener(e -> {
            dispose();
            App.getInstance().getAdProductsView().setVisible(true);
        });
        btnManageUsers.addActionListener(e -> {
            dispose();
            App.getInstance().getAdUserView().setVisible(true);
        });
        btnManageOrders.addActionListener(e -> {
            dispose();
            App.getInstance().getAdOrdersView().setVisible(true);
        });
        /*btnRefresh.addActionListener(e -> {
        });*/
        btnLogout.addActionListener(e -> logout());

    }

    public JButton getBtnRefresh(){return btnRefresh;}

    public void displayProductInfo(Map<String, Integer> categCount){
        productInfoPanel.removeAll();
        JLabel title = new JLabel("Total Products: ");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        productInfoPanel.add(title);

        for (Map.Entry<String, Integer> entry : categCount.entrySet()) {
            JLabel totalLine = new JLabel(entry.getKey()+ ": " + entry.getValue());
            totalLine.setFont(new Font("Arial", Font.PLAIN, 15));
            productInfoPanel.add(totalLine);
        }
        productInfoPanel.revalidate();
        productInfoPanel.repaint();
        //contentPanel.add(productInfoPanel);
    }

    public void displayUserInfo (Map<String, Integer> userInfo) {
        userInfoPanel.removeAll();
        JLabel title = new JLabel("Total Users: ");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        userInfoPanel.add(title);

        for (Map.Entry<String, Integer> entry : userInfo.entrySet()) {
            JLabel totalLine = new JLabel(entry.getKey()+ ": " + entry.getValue());
            totalLine.setFont(new Font("Arial", Font.PLAIN, 15));
            userInfoPanel.add(totalLine);
        }
        userInfoPanel.revalidate();
        userInfoPanel.repaint();
    }

    public void displaySales(double totalSales) {
        saleLabel.setText("Total sales: "+ totalSales);
        saleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();  // Close dashboard
            Session.getInstance().clearSession();
            System.out.println("Session cleared!");
            App.getInstance().getHomeScreen().setVisible(true);  // Replace with login screen
        }
    }
}