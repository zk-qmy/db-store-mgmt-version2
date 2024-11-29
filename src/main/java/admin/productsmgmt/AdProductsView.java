package admin.productsmgmt;

import orders.OrderDetailsDAO;
import orders.OrdersDAO;
import products.Products;
import products.ProductsDAO;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdProductsView  extends JFrame {
    private JComboBox<String> categoryComboBox;
    private JPanel comboBoxPan;
    private JButton btnFind = new JButton("Find product");
    private JButton btnBrowse = new JButton(("See all Products"));
    private JButton btnAdd = new JButton("Add Product"); // navigate to cart view
    private JButton btnDelete = new JButton("Delete Product");
    private JButton btnUpdate = new JButton("Update product");
    private JPanel productPan;
    private JButton btnBack = new JButton("Back");

    public AdProductsView () {
        this.setTitle("Our Products");
        this.setLayout(new BorderLayout());
        this.setSize(1020, 800);
        this.setLocationRelativeTo(null);

        //ComboBox
        categoryComboBox = new JComboBox<>();
        comboBoxPan = new JPanel();
        comboBoxPan.add(categoryComboBox);

        // LabelTit
        JLabel labelTit = new JLabel("Manage Inventory");
        labelTit.setBorder(new EmptyBorder(20, 10, 40, 20));
        labelTit.setFont(new Font("Arial", Font.BOLD, 50));

        // Parent panel - border
        JPanel parentPan = new JPanel();
        parentPan.setLayout(new BorderLayout());
        // Control panel - border
        JPanel controlPan = new JPanel();
        controlPan.setLayout(new BoxLayout(controlPan, BoxLayout.Y_AXIS));
        controlPan.setBorder(new EmptyBorder(40, 20, 40, 20));

        // Center the buttons and add them to the control panel
        btnFind.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBrowse.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDelete.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpdate.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to control panel
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        //controlPan.add(Box.createVerticalStrut(20)); // Spacer for spacing between elements
        controlPan.add(btnFind);
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        controlPan.add(btnBrowse);
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        controlPan.add(btnAdd);
        controlPan.add(Box.createVerticalStrut(10)); // Spacer
        controlPan.add(btnDelete);
        controlPan.add(Box.createVerticalStrut(10));
        controlPan.add(btnUpdate);
        controlPan.add(Box.createVerticalStrut(10));
        controlPan.add(comboBoxPan);
        controlPan.add(btnBack);

        // Product panel - grid
        productPan = new JPanel();
        productPan.setLayout(new GridLayout(0, 4, 10, 30)); // dynamic rows, 4 cols
        productPan.setBorder(new EmptyBorder(0, 20, 20, 10));
        // Scrollable Pane
        JScrollPane scrollPan = new JScrollPane(productPan);
        scrollPan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPan.setPreferredSize(new Dimension(600, 400));

        parentPan.add(controlPan, BorderLayout.EAST);
        parentPan.add(scrollPan, BorderLayout.CENTER);
        scrollPan.setViewportView(productPan);
        this.getContentPane().add(parentPan, BorderLayout.CENTER);
        this.getContentPane().add(labelTit, BorderLayout.NORTH);
    }
    public JComboBox<String> getCategoryComboBox() {return categoryComboBox;}
    public JButton getBtnFind() {return btnFind;}
    public JButton getBtnBrowse(){return btnBrowse;}
    public JButton getBtnAdd() {return btnAdd;}
    public JButton getBtnDelete() {return btnDelete;}
    public JButton getBtnUpdate() {return btnUpdate;}
    public JButton getBtnBack() {return btnBack;}

    public void displayProducts(List<Products> productsList) {
        // clear existing components
        productPan.removeAll();

        // Create product box
        Border boxBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        // add product to panel
        if (productsList.isEmpty()) {
            JLabel emptyList = new JLabel(" No product available!");
        } else {
            // Add each product to the product panel
            for (Products product : productsList) {
                JPanel box = new JPanel();
                box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
                box.setBorder(boxBorder);
                box.setPreferredSize(new Dimension(150, 200));

                // Product info labels
                JLabel productID = new JLabel("ID: " + product.getProductID());
                JLabel productName = new JLabel("Name: " + product.getProductName());
                JLabel productPrice = new JLabel("Price: $" + product.getPrice());
                JLabel productQuantity = new JLabel("In Stock: " + product.getStockQuantity());

                // Font adjustments for better readability
                productID.setFont(new Font("Arial", Font.BOLD, 15));
                productName.setFont(new Font("Arial", Font.BOLD, 20));
                productPrice.setFont(new Font("Arial", Font.PLAIN, 18));
                productQuantity.setFont(new Font("Arial", Font.PLAIN, 18));

                // Center product name and price
                productName.setAlignmentX(Component.CENTER_ALIGNMENT);
                productPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
                productQuantity.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Add product information to the box
                box.add(productID);
                box.add(productName);
                box.add(productPrice);
                box.add(productQuantity);

                // Add each box to the main product panel
                productPan.add(box);
            }
        }
        productPan.revalidate();
        productPan.repaint();
    }

    public void displaySelectedProduct(Products product) {
        // clear existing components
        productPan.removeAll();

        // Create product box
        Border boxBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        // add product to panel
        // TO DO: adjust this
        if (product == null) {
            JLabel nullProduct = new JLabel(" Product does not exist!");
        } else {
            JPanel box = new JPanel();
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            box.setBorder(boxBorder);
            box.setPreferredSize(new Dimension(150, 200)); // Adjust the size for better appearance

            // Product info labels
            JLabel productID = new JLabel("ID: " + product.getProductID());
            JLabel productName = new JLabel("Name: " + product.getProductName());
            JLabel productPrice = new JLabel("Price: $" + product.getPrice());
            JLabel productQuantity = new JLabel("In Stock: " + product.getStockQuantity());

            // Font adjustments for better readability
            productID.setFont(new Font("Arial", Font.BOLD, 15));
            productName.setFont(new Font("Arial", Font.BOLD, 20));
            productPrice.setFont(new Font("Arial", Font.PLAIN, 18));
            productQuantity.setFont(new Font("Arial", Font.PLAIN, 18));

            // Center product name and price
            productName.setAlignmentX(Component.CENTER_ALIGNMENT);
            productPrice.setAlignmentX(Component.CENTER_ALIGNMENT);
            productQuantity.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Add product information to the box
            box.add(productID);
            box.add(productName);
            box.add(productPrice);
            box.add(productQuantity);

            // Add each box to the main product panel
            productPan.add(box);
        }
        productPan.revalidate();
        productPan.repaint();
    }
}