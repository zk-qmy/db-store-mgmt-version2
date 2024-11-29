package customer.cart;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import orders.OrderDetailsDAO;
import products.Products;
import products.ProductsDAO;
import register.Session;

public class CartController implements ActionListener {
    private CartView view;
    private OrderDetailsDAO orderDetailsDAO;
    private ProductsDAO productsDao;
    private Cart cart = null;

    public CartController(CartView view, OrderDetailsDAO orderDetailsDAO, ProductsDAO productsDao) {
        this.orderDetailsDAO = orderDetailsDAO;
        this.view = view;
        this.productsDao = productsDao;

        view.getBtnAdd().addActionListener(this);
        view.getBtnOrder().addActionListener(this);

        cart = new Cart();
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAdd()){
            addProduct();
        }
        else if (e.getSource() == view.getBtnOrder()) {
            placeOrder();
            view.setVisible(false);
        }
    }

    public void placeOrder(){
        // TO DO: show order placed message + save the order to Orders table + all lines to OrderDetails. if not
        // hit the button, do not save the info yet.
        // update new orderQuantity!!
        List<CartLines> cartLinesList = cart.getLines();
        if (cartLinesList == null || cartLinesList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please add products you want to buy!");
            return;
        }
        for (CartLines line: cart.getLines()) {
            int productID = line.getProductID();
            Products product = productsDao.findProductbyID(productID);

            int newStockQuantity = product.getStockQuantity() - line.getOrderQuantity();
            //product.setStockQuantity(newStockQuantity);
            productsDao.updateProductStock(newStockQuantity, productID);
        }
        int defaultStatusID = 2;  //"pending"
        int customerID = Session.getInstance().getCurrentUser().getUserID();
        System.out.println("User with id: "+ customerID+" placed order!!!!!!!!!!");

        // Save the cart and its details into the database
        boolean success = orderDetailsDAO.addToOrderDB(cart, defaultStatusID, customerID);
        // Optionally, clear the cart or show a success message
        if (!success) {
            JOptionPane.showMessageDialog(null, "Failed to place this order!");
            return;
        }
        JOptionPane.showMessageDialog(null, "Order placed successfully!");
        // Clear the cart
        cart = new Cart();
        view.clearCartTable();
        view.getLabTotal().setText("Total: $0");
    }


    private void addProduct(){
        // Validate productID
        int productID = 0;
        // check valid number format
        String id = JOptionPane.showInputDialog("Enter ProductID: ");
        try {
            productID = Integer.parseInt(id);
        }catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid product ID!");
            return;
        }
        // check exist ID
        List<Integer> productKeys = productsDao.getProductKeyID();
        if(!productKeys.contains(productID)) {
            JOptionPane.showMessageDialog(null, "Product ID does not exist!!");
            return;
        }
        // check unique item in cart
        if (!isUniqueItemInCart(productID)) {
            JOptionPane.showMessageDialog(null, "This product already existed in cart!");
            return;
        }

        Products product = productsDao.findProductbyID(Integer.parseInt(id));
        int orderQuantity = 0;
        String quantity = JOptionPane.showInputDialog(null,"Enter orderQuantity: ");
        try {
            orderQuantity = Integer.parseInt(quantity);
        }catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid quantity!");
            return;
        }
        if (orderQuantity <= 0 ) {
            JOptionPane.showMessageDialog(null, "This orderQuantity is not valid!");
            return;
        } else if (orderQuantity > product.getStockQuantity()) {
            JOptionPane.showMessageDialog(null, "Only " + product.getStockQuantity() + " products left!");
            return;
        } else if (orderQuantity == product.getStockQuantity()) {
            JOptionPane.showMessageDialog(null, "Purchase not allowed: Please leave at least one product in stock!");
            return;
        }

        CartLines line = new CartLines();
        line.setCartID(this.cart.getCartID());
        line.setProductID(product.getProductID());
        line.setOrderQuantity(orderQuantity);
        line.setCost(orderQuantity * product.getPrice());
        cart.getLines().add(line);
        cart.setTotalPayment(cart.getTotalPayment() + line.getCost());


        Object[] row = new Object[5];
        row[0] = line.getProductID();
        row[1] = product.getProductName();
        row[2] = product.getPrice();
        row[3] = line.getOrderQuantity();
        row[4] = line.getCost();

        this.view.addRow(row);
        this.view.getLabTotal().setText("Total: $" + cart.getTotalPayment());
        this.view.invalidate();
    }

    private boolean isUniqueItemInCart(int productID) {
        List<CartLines> cartItems = cart.getLines();
        List<Integer> cartItemIDList = new ArrayList<>();
        for (CartLines line: cartItems) {
            cartItemIDList.add(line.getProductID());
        }
        if (cartItemIDList.contains(productID)) {
            return false;
        }
        return true;
    }

}