package customer.orderhistory;

import app.App;
import orders.Orders;
import orders.*;
import org.bson.types.ObjectId;
import register.Session;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PrivilegedAction;
import java.util.List;
import javax.swing.*;

public class OrderHisController implements ActionListener{
    private OrderHisView view;
    //private OrdersDAO ordersCollection;
    private OrdersCollection ordersCollection;

    public OrderHisController(OrderHisView view, OrdersCollection ordersCollection) {
        this.view = view;
        //this.ordersCollection = ordersCollection;
        this.ordersCollection = ordersCollection;
        loadOrderList();

        view.getBtnCancel().addActionListener(this);
        view.getBtnRefresh().addActionListener(this);
        view.getBtnBack().addActionListener(this);
                //order = new Orders();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnCancel()){
            cancelOrder();
        } else if (e.getSource() == view.getBtnRefresh()) {
            loadOrderList();
        } else if (e.getSource() == view.getBtnBack()) {
            view.dispose();
            App.getInstance().getBrowserView().setVisible(true);
        }
    }

    public void cancelOrder() { // TO DO: [Fix] add back the quantity to the stock
        String objectIdString = JOptionPane.showInputDialog("Enter OrderID: ");
        ObjectId orderID;
        if(objectIdString==null||objectIdString.isEmpty()){
            return;
        }
        try {
            orderID = new ObjectId(objectIdString);
        }catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Invalid orderID format! Please copy the orderID");
            return;
        }
        int userID = Session.getInstance().getCurrentUser().getUserID();

        Orders validOrder = ordersCollection.loadOrdersByID(orderID);
        if (validOrder == null) {
            JOptionPane.showMessageDialog(null, "orderID does not exist!");
            return;
        }

        String cancelStatus = "cancelled"; //"cancelled"
        boolean success = ordersCollection.updateStatus(orderID, cancelStatus);
        if (success) {
            JOptionPane.showMessageDialog(null, "Canceled successfully!");
            loadOrderList();
            System.out.println("Canceled ordered by user "+userID +"!!!");
        } else {
            JOptionPane.showMessageDialog(null, "Failed to cancel order!");
        }
    }

    public void loadOrderList() {
        int userID = Session.getInstance().getCurrentUser().getUserID();
        //List<Orders> ordersList = ordersCollection.loadUserOrders(userID);
        List<Orders> ordersList = ordersCollection.loadUserOrders(userID);
        if (ordersList.isEmpty() || ordersList == null) {
            System.out.println("order list empty!!!");
        }
        view.displayOrders(ordersList);
    }

    public void btnReviewAction(ObjectId orderID){
        // load a list of product based on orderID
        // pass in the orderID so that the review can load the product of the right order
        view.dispose();
        App.getInstance().getReviewView().setVisible(true);
        App.getInstance().getReviewView().getCurrentOrderID(orderID);
        App.getInstance().getReviewController();

    }

}