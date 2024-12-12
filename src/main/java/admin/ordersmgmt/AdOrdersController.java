package admin.ordersmgmt;

import app.App;
import customer.reviews.ReviewDAO;
import orders.Orders;
import orders.OrdersCollection;
import org.bson.types.ObjectId;
import register.Session;
import register.users.Users;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class AdOrdersController implements ActionListener {
    private AdOrdersView view;
    //private OrdersDAO ordersDAO;
    private OrdersCollection ordersCollection;
    private ReviewDAO reviewDAO;

    public AdOrdersController(AdOrdersView view, OrdersCollection ordersCollection, ReviewDAO reviewDAO) {
        this.view = view;
        this.ordersCollection = ordersCollection;
        this.reviewDAO = reviewDAO;
        displayOrders();
        hideBackButton();
        hideLogOutButton();

        view.getBtnAddOrder().addActionListener(this);
        view.getBtnDeleteOrder().addActionListener(this);
        view.getBtnUpdateOrder().addActionListener(this);
        view.getBtnBack().addActionListener(this);
        view.getBtnRefresh().addActionListener(this);
        view.getBtnLogOut().addActionListener(this);
        //System.out.println("INIT ADORDERCONTROLLER");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnAddOrder()) {
            App.getInstance().getCartView().setVisible(true);
        } else if (e.getSource() == view.getBtnUpdateOrder()) {
            updateOrder();
        } else if(e.getSource() == view.getBtnDeleteOrder()) {
            deleteOrder();
        } else if (e.getSource() == view.getBtnBack()) {
            view.dispose();
            App.getInstance().getDashBoardView().setVisible(true);
        } else if (e.getSource()==view.getBtnRefresh()) {
            displayOrders();
        } else if(e.getSource() == view.getBtnLogOut()){
            logout();
        }
    }

    public void displayOrders(){
        List<Orders> orderList = ordersCollection.loadAllOrders();
        //debug
        if(orderList == null| orderList.isEmpty()){
            System.out.println("AdOrdersController: Empty orderList!");
        }
        //
        view.getTableModel().setRowCount(0);

        for (Orders order : orderList) {
            view.getTableModel().addRow(new Object[] {
                    order.getOrderID(),
                    order.getUserID(),
                    order.getCtmName(),
                    order.getTotal(),
                    order.getStatus(),
                    order.getAddress(),
                    order.getPhone()
            });
        }
    }
    public void deleteOrder(){
        String idString = JOptionPane.showInputDialog(null, "Paste ID of the order you want to delete: ");
        ObjectId orderID;
        try {
            orderID = new ObjectId(idString);
        }catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Invalid orderID format! Please copy the orderID");
            return;
        }
        if(!validOrderID(orderID)){
            return;
        };
        // check if this order is not cancelled
        checkStatus(orderID);

    }
    private void checkStatus(ObjectId orderID){
        Orders targetOrder = ordersCollection.loadOrdersByID(orderID);
        String dbStatus = targetOrder.getStatus();
        if (dbStatus == null || dbStatus.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Cannot find Order Status!");
            return;
        }
        if (!dbStatus.equals("cancelled") && !dbStatus.equals("processed")){
            int confirm = JOptionPane.showConfirmDialog(null,
                    "This Order is being " + dbStatus + ". Do you still want to delete? ",
                        "Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ordersCollection.deleteOrder(orderID);
                // delete its coresponding review.
                reviewDAO.deleteReview(orderID);
                displayOrders();
            }
        } else {
            ordersCollection.deleteOrder(orderID);
            displayOrders();
        }
    }
    public void updateOrder(){
        // orderID input
        String objectIdString = JOptionPane.showInputDialog("Paste orderID here: ");
        ObjectId orderID;
        try {
            orderID = new ObjectId(objectIdString);
        }catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "Invalid orderID format! Please copy the orderID");
            return;
        }
        if(!validOrderID(orderID)){
            return;
        };

        // status input
        String status = JOptionPane.showInputDialog(null,
                "Enter order status: \n 1. approved \n 2. pending \n 3. shipped \n 4. processed \n 5. cancelled");
        if (status == null){
            JOptionPane.showMessageDialog(null, "Please write down the status!");
            return;
        }
        status = status.toLowerCase();
        if(isNotValidStatus(status)){
            JOptionPane.showMessageDialog(null,"Invalid status!");
            return;
        };
        boolean success = ordersCollection.updateStatus(orderID, status);
        if(success) {
            JOptionPane.showMessageDialog(null, "Status updated");
            displayOrders();
        } else {
            JOptionPane.showMessageDialog(null, "Failed to update status!");
        }
    }
    private boolean isNotValidStatus(String status) {
        // Convert array to a list
        List<String> validStatus = Arrays.asList("approved", "pending", "shipped", "processed", "cancelled");
        return !validStatus.contains(status);
    }
    private boolean validOrderID(ObjectId orderID){
        Orders validOrder = ordersCollection.loadOrdersByID(orderID);
        if (validOrder == null) {
            JOptionPane.showMessageDialog(null, "orderID does not exist!");
            return false;
        }
        return true;
    }

    private void hideBackButton(){ // hide back button from cashier
        Users currentUser = Session.getInstance().getCurrentUser();

        if (currentUser != null) {
            System.out.println(currentUser.getRoleID());
            int currentRoleID = currentUser.getRoleID();
            if (currentRoleID == 3){
                view.getBtnBack().setVisible(false);
                view.getControlPan().revalidate();
                view.getControlPan().repaint();
            }
        }
    }
    private void hideLogOutButton(){ // hide logout button from admin
        Users currentUser = Session.getInstance().getCurrentUser();

        if (currentUser != null) {
            System.out.println(currentUser.getRoleID());
            int currentRoleID = currentUser.getRoleID();
            if (currentRoleID == 1){
                view.getBtnLogOut().setVisible(false);
                view.getControlPan().revalidate();
                view.getControlPan().repaint();
            }
        }
    }
    private void logout(){
        int confirm = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            view.dispose();
            Session.getInstance().clearSession();
            System.out.println("Session cleared!");
            App.getInstance().getHomeScreen().setVisible(true);  // Replace with login screen
        }
    }

}