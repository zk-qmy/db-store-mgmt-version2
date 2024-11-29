package register.createaccount;

import app.App;
import app.HomeScreen;
import register.Session;
import register.users.Users;
import register.users.UsersDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RegisterController implements ActionListener {
    private RegisterView view;
    private UsersDAO usersDAO;
    private HomeScreen homeScreen;

    public RegisterController(RegisterView view, UsersDAO userDAO, HomeScreen homeScreen) {
        this.view = view;
        this.usersDAO = userDAO;
        this.homeScreen = homeScreen;
        view.getBtnRegister().addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnRegister()) {
            //System.out.println("Register button triggered!!");
            String name = view.getNameField().getText().trim();
            if(!isValidName(name)) {
                JOptionPane.showMessageDialog(null, "Invalid name!");
                return;
            }

            String address = view.getAddressField().getText().trim();
            if(!isValidString(address)) {
                JOptionPane.showMessageDialog(null, "Invalid address!");
                return;
            }
            String phone = view.getPhoneField().getText().trim();
            if (!isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(null, "Invalid phone number!");
                return;
            }

            String username = view.getUsernameField().getText().trim();
            if(!isValidString(username)) {
                JOptionPane.showMessageDialog(null, "Invalid username!");
                return;
            }
            // check duplicate(unique) username
            if (isNotUniqueUsername(username)){
                JOptionPane.showMessageDialog(null, "Username already exist!");
                return;
            }

            String password = new String(view.getPasswordField().getPassword());
            if(!isValidString(password)) {
                JOptionPane.showMessageDialog(null, "Invalid password!");
                return;
            }
            String confirmPassword = new String(view.getConfirmPasswordField().getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                int roleID = 2; // default customer id is 2
                // Implement registration logic here
                boolean isCreated = usersDAO.addUser(name, username, password, address, phone, roleID);
                if (isCreated) {
                    JOptionPane.showMessageDialog(null, "Account created successfully!");
                    Users currentUser = usersDAO.getUserByUsername(username);
                    Session.getInstance().setCurrentUser(currentUser);
                    System.out.println("Session created for user: " + currentUser.getUserID());
                    homeScreen.dispose();
                    view.dispose();
                    App.getInstance().getOrderHisController();
                    App.getInstance().getBrowserView().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create account!");
                }

            }
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
    private boolean isNotUniqueUsername(String username) {
        List<String> usernameList = usersDAO.getAllUsernames();
        if (usernameList != null && !usernameList.isEmpty()){
            return usernameList.contains(username);
        }
        return false;
    }
}