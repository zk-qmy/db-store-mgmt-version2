package register.login;

import app.App;
import app.HomeScreen;
import register.Session;
import register.users.Users;
import register.users.UsersDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController implements ActionListener {
    private LoginView view;
    private UsersDAO usersDAO;
    private HomeScreen homeScreen;

    public LoginController(LoginView view, UsersDAO usersDAO, HomeScreen homeScreen) {
        this.view = view;
        this.usersDAO = usersDAO;
        this.homeScreen = homeScreen;

        view.getBtnLogin().addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getBtnLogin()) {
            String username = view.getUsernameField().getText().trim();
            String password = new String(view.getPasswordField().getPassword());
            Users currentUser = usersDAO.getUserByUsername(username);
            if (currentUser == null) {
                JOptionPane.showMessageDialog(null, "User does not exist!");
                return;
            }
            String tempUserPassword = currentUser.getPassword();
            if (!tempUserPassword.equals(password)) {
                JOptionPane.showMessageDialog(null, "Wrong password!");
                return;
            }

            System.out.println("Login with username = " + username + " and password = ***"); //password);
            Session.getInstance().setCurrentUser(currentUser); // Store session
            System.out.println("Session of user: " + currentUser.getUserID());
            view.dispose();
            homeScreen.dispose();
            if (currentUser.getRoleID() == 1) { // admin
                App.getInstance().getDashBoardView().setVisible(true);
            } else if (currentUser.getRoleID() == 2) { // customer
                App.getInstance().getBrowserView().setVisible(true);
                App.getInstance().getOrderHisController();
            }
            /*Users user = usersDAO.loadUser(username, password);

            if (user == null) {
                JOptionPane.showMessageDialog(null, "This user does not exist!");
            }
            else {
                Users currentUser = usersDAO.getUserByUsername(username);
                Session.getInstance().setCurrentUser(currentUser); // Store session
                System.out.println("Session of user: " + currentUser.getUserID());
                view.dispose();
                homeScreen.dispose();
                if (user.getRoleID() == 1){ // admin
                    App.getInstance().getDashBoardView().setVisible(true);
                } else if (user.getRoleID() == 2) { // customer
                    App.getInstance().getBrowserView().setVisible(true);
                    App.getInstance().getOrderHisController();
                }
            */

        }
    }
}

