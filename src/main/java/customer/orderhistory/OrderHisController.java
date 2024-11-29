package customer.orderhistory;

import app.App;
import orders.Orders;
import orders.*;
import register.Session;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;

public class OrderHisController implements ActionListener{
    private OrderHisView view;
    private OrdersDAO ordersDao;

    public OrderHisController(OrderHisView view, OrdersDAO ordersDao) {
        this.view = view;
        this.ordersDao = ordersDao;
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
        int orderID = 0;
        String id = JOptionPane.showInputDialog("Enter OrderID: ");
        try {
            orderID = Integer.parseInt(id);
        }catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Order ID!");
            return;
        }
        int userID = Session.getInstance().getCurrentUser().getUserID();
        List<Integer> orderIDList = ordersDao.getUserOrderID(userID);
        if (!orderIDList.contains(orderID)) {
            JOptionPane.showMessageDialog(null, "orderID does not exist!");
            return;
        }
        int cancelStatusID = 5; //"canceled"
        boolean success = ordersDao.updateStatus(orderID, cancelStatusID);
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
        List<Orders> ordersList = ordersDao.loadUserOrders(userID);
        if (ordersList.isEmpty()) {
            System.out.println("order list empty!!!");
        }
        view.displayOrders(ordersList);
    }

}