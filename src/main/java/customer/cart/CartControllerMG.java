package customer.cart;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.ClientSession;
import connections.MongoDbConnection;
import customer.reviews.ReviewDAO;
import orders.Orders;
import orders.OrdersCollection;
import org.bson.types.ObjectId;
import products.Products;
import products.ProductsDAO;
import register.Session;

public class CartControllerMG implements ActionListener {
    private CartView view;
    private OrdersCollection ordersCollection;
    private ReviewDAO reviewDAO;
    private ProductsDAO productsDao;
    private Cart cart;

    public CartControllerMG(CartView view, OrdersCollection ordersCollection, ProductsDAO productsDao, ReviewDAO reviewDAO) {
        this.view = view;
        this.productsDao = productsDao;
        this.ordersCollection = ordersCollection;
        this.reviewDAO = reviewDAO;

        view.getBtnAdd().addActionListener(this);
        view.getBtnOrder().addActionListener(this);

        cart = new Cart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAdd()) {
            addProduct();
        } else if (e.getSource() == view.getBtnOrder()) {
            placeOrder();
            view.setVisible(false);
        }
    }

    public void placeOrder() {
        List<CartLines> cartLinesList = cart.getLines();
        if (cartLinesList == null || cartLinesList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please add products you want to buy!");
            return;
        }
        // Start MongoDB transaction
        try (ClientSession mongoSession = MongoDbConnection.getInstance().getMongoClient().startSession()) {
            mongoSession.startTransaction();
            List<Integer> productIDList = new ArrayList<>();
            try {
                // Update SQL stock quantities
                for (CartLines line : cartLinesList) {
                    int productID = line.getProductID();
                    productIDList.add(productID);
                    Products product = productsDao.findProductbyID(productID);

                    // Calculate new stock
                    int newStockQuantity = product.getStockQuantity() - line.getOrderQuantity();
                    if (newStockQuantity < 0) {
                        throw new IllegalArgumentException("Insufficient stock for product ID: " + productID);
                    }
                    // Update stock in SQL
                    productsDao.updateProductStock(newStockQuantity, productID);
                }

                // Save order to MongoDB
                int userID = Session.getInstance().getCurrentUser().getUserID();
                double totalPayment = cart.getTotalPayment();

                // Add order
                ObjectId orderID = ordersCollection.addOrderNGetID(userID, totalPayment, cartLinesList, mongoSession);
                // Commit MongoDB transaction
                mongoSession.commitTransaction();
                // Validate adding process
                Orders currentOrder= ordersCollection.loadOrdersByID(orderID);
                if(currentOrder == null){
                    JOptionPane.showMessageDialog(null,"Failed to place order!");
                    return;
                }else {
                    try {
                        // Add review section
                        for (int productID : productIDList) {
                            reviewDAO.addReview(productID, userID, orderID, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    // Success message
                    JOptionPane.showMessageDialog(null, "Order placed successfully!");
                }
                // Clear the cart
                cart = new Cart();
                view.clearCartTable();
                view.getLabTotal().setText("Total: $0");

            } catch (Exception ex) {
                // Rollback MongoDB transaction
                mongoSession.abortTransaction();
                JOptionPane.showMessageDialog(null, "Failed to place the order: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void addProduct() {
        // Validate product ID
        int productID;
        String id = JOptionPane.showInputDialog("Enter ProductID: ");
        try {
            productID = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid product ID!");
            return;
        }

        // Check if product exists in SQL
        List<Integer> productKeys = productsDao.getProductKeyID();
        if (!productKeys.contains(productID)) {
            JOptionPane.showMessageDialog(null, "Product ID does not exist!");
            return;
        }

        // Check unique item in cart
        if (!isUniqueItemInCart(productID)) {
            JOptionPane.showMessageDialog(null, "This product already exists in the cart!");
            return;
        }

        Products product = productsDao.findProductbyID(productID);

        // Get order quantity
        int orderQuantity;
        String quantity = JOptionPane.showInputDialog(null, "Enter order quantity: ");
        try {
            orderQuantity = Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid quantity!");
            return;
        }

        if (orderQuantity <= 0) {
            JOptionPane.showMessageDialog(null, "Order quantity is not valid!");
            return;
        } else if (orderQuantity > product.getStockQuantity()) {
            JOptionPane.showMessageDialog(null, "Only " + product.getStockQuantity() + " products left!");
            return;
        }

        // Add product to the cart
        CartLines line = new CartLines();
        line.setCartID(cart.getCartID());
        line.setProductID(product.getProductID());
        line.setOrderQuantity(orderQuantity);
        line.setCost(orderQuantity * product.getPrice());
        cart.getLines().add(line);
        cart.setTotalPayment(cart.getTotalPayment() + line.getCost());

        // Update UI
        Object[] row = new Object[5];
        row[0] = line.getProductID();
        row[1] = product.getProductName();
        row[2] = product.getPrice();
        row[3] = line.getOrderQuantity();
        row[4] = line.getCost();

        view.addRow(row);
        view.getLabTotal().setText("Total: $" + cart.getTotalPayment());
        view.invalidate();
    }

    private boolean isUniqueItemInCart(int productID) {
        return cart.getLines().stream().noneMatch(line -> line.getProductID() == productID);
    }
}
