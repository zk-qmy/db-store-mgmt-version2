package admin.usersmgmt;

import app.App;
import orders.Orders;
import orders.OrdersCollection;
import org.bson.types.ObjectId;
import register.users.Users;
import register.users.UsersDAO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdUserController implements ActionListener {
    private AdUserView view;
    private UsersDAO usersDAO;
    private OrdersCollection ordersCollection;
    // private OrdersDAO ordersCollection;

    public AdUserController(AdUserView view, UsersDAO usersDAO, OrdersCollection ordersCollection){
        this.view = view;
        this.usersDAO = usersDAO;
        this.ordersCollection = ordersCollection;
        displayUsers();

        view.getBtnAddUser().addActionListener(this);
        view.getBtnUpdateUser().addActionListener(this);
        view.getBtnDeleteUser().addActionListener(this);
        view.getBtnBack().addActionListener(this);
        view.getBtnRefresh().addActionListener(this);
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == view.getBtnAddUser()) {
            System.out.println("user add Button triggered");
            boolean add=true;
            addNupdateUser(add);
        } else if (e.getSource() == view.getBtnUpdateUser()) {
            boolean add=false;
            addNupdateUser(add);
        } else if (e.getSource() == view.getBtnDeleteUser()) {
            deleteUser();
        } else if (e.getSource() == view.getBtnBack()) {
            view.dispose();
            App.getInstance().getDashBoardView().setVisible(true);
        } else if(e.getSource() == view.getBtnRefresh()) {
            displayUsers();
        }
    }

    public void addNupdateUser(boolean add) {
        String name = JOptionPane.showInputDialog("Enter full name: ");
        if(!isValidName(name)) {
            JOptionPane.showMessageDialog(null, "Invalid name!");
            return;
        }
        // update username is not allowed
        String username = null;
        if (add) {
            username = JOptionPane.showInputDialog("Enter username: ");
            if(!isValidString(username)) {
                JOptionPane.showMessageDialog(null, "Invalid username!");
                return;
            }
            // check duplicate username
            if (isNotUniqueUsername(username)) {
                JOptionPane.showMessageDialog(null, "Username already exist!");
                return;
            }
        }

        String password = JOptionPane.showInputDialog("Enter password: ");
        if(!isValidString(password)) {
            JOptionPane.showMessageDialog(null, "Invalid password!");
            return;
        }
        String address = JOptionPane.showInputDialog("Enter address: ");
        if(!isValidString(address)) {
            JOptionPane.showMessageDialog(null, "Invalid address!");
            return;
        }
        String phone = JOptionPane.showInputDialog("Enter phone: ");
        if (!isValidPhoneNumber(phone)) {
            JOptionPane.showMessageDialog(null, "Invalid phone number");
            return;
        }
        int roleID = 0;
        String roleid = JOptionPane.showInputDialog("Enter roleID:\n 1. Admin \n 2. Customer");
        try{
            roleID = Integer.parseInt(roleid);
        }catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }

        boolean success;
        int userID = 0;
        if (add) {
            success = usersDAO.addUser(name,username,password,address,phone,roleID);
            displayUsers();
            confirmProcess(success,"add");
        } else {
            String userid = JOptionPane.showInputDialog("Enter user id: ");
            try {
                userID = Integer.parseInt(userid);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            success = usersDAO.updateUser(userID, name,password,address,phone,roleID);
            displayUsers();
            confirmProcess(success, "update");

        }
    }
    private boolean isValidName(String string) {
        // Regex to check if the string contains only letters, digits, and spaces
        return string != null && !string.trim().isEmpty() && string.matches("^[a-zA-Z0-9\\s]+$");
    }
    private boolean isValidString(String string) {
        return string != null && !string.trim().isEmpty();
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length()>=10 && phoneNumber.length()<=15) {
            // Regex to check if phone number starts with optional +, followed by digits, spaces, dashes
            return phoneNumber.matches("^[+]?\\d{1,4}?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,9}$");
        }
        return false;
    }
    public void confirmProcess(boolean success, String task){
        // Confirm process
        if (success) {
            JOptionPane.showMessageDialog(null, "User " + task + "d!");
            return;
        } else {
            JOptionPane.showMessageDialog(null, "Failed to "+ task +" user!");
            return;
        }
    }

    public void displayUsers() {
        List<Users> userList = usersDAO.loadAllUsers();

        view.getTableModel().setRowCount(0);

        for (Users user : userList) {
            view.getTableModel().addRow(new Object[] {
                    user.getUserID(),
                    user.getName(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getAddress(),
                    user.getPhoneNum(),
                    user.getRoleName()
            });
        }
    }

    public void deleteUser() {
        // check valid userID
        // if userID = 1 (ultimate admin -> not allow)
        String userid = JOptionPane.showInputDialog("Enter user id that you want to delete: ");
        int userID = 0;
        try {
            userID = Integer.parseInt(userid);
        } catch(NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid user id");
            return;
        }
        if (userID == 1){
            JOptionPane.showMessageDialog(null, "Cannot delete Ultimate Admin account!");
            return;
        }
        // check if this user has on going orders
        boolean success = checkProcessOrders(userID);
        displayUsers();
        confirmProcess(success, "delete");
    }
    private boolean checkProcessOrders(int userID){
        List<Orders> ordersList = usersDAO.getProcessOrders(userID);
        if (ordersList != null && !ordersList.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "This will delete all on going Orders of this User too. Do you want to delete all?",
                    "Delete All", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // delete order, then delete user
                for (Orders order: ordersList) {
                    ObjectId orderID = order.getOrderID();
                    ordersCollection.deleteOrder(orderID);
                }
            } else if (confirm == JOptionPane.NO_OPTION) { // add this to avoid causing error with database
                return false;
            }
        }
        // else, no order exist yet => just delete user
        // delete user
        return usersDAO.deleteUser(userID);
    }

    private boolean isNotUniqueUsername(String username) {
        List<String> usernameList = usersDAO.getAllUsernames();
        if (usernameList != null && !usernameList.isEmpty()){
            return usernameList.contains(username);
        }
        return false;
    }

}