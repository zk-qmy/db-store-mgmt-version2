package admin.dashboard;


import orders.OrdersCollection;
import products.ProductsDAO;
import register.users.UsersDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class DashBoardController implements ActionListener {
    private DashBoardView view;
    private ProductsDAO productsDAO;
    private OrdersCollection ordersCollection;
    private UsersDAO usersDAO;


    public DashBoardController (DashBoardView view,
                                ProductsDAO productsDAO,
                                OrdersCollection ordersCollection,
                                UsersDAO usersDAO){
        this.view = view;
        this.productsDAO = productsDAO;
        this.usersDAO = usersDAO;
        this.ordersCollection = ordersCollection;

        loadBestSellingText();
        loadProductCount();
        loadNewCreatedUser();
        loadUserCount();
        loadSales();
        view.getBtnRefresh().addActionListener(this);
        view.getBtnResetBestSelling().addActionListener(this);
        view.getBtnResetUserTracker().addActionListener(this);
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == view.getBtnRefresh()) {
            // display report screen
            loadProductCount();
            loadUserCount();
            loadSales();
        } else if (e.getSource() == view.getBtnResetBestSelling()) {
            resetBestSelling();
            loadProductCount();
            //loadUserCount();
            loadSales();
        } else if (e.getSource() == view.getBtnResetUserTracker()) {
            resetUserTracker();
            //loadProductCount();
            loadUserCount();
            //loadSales();
        }
    }

    public void resetBestSelling(){
        ordersCollection.resetBestSelling();
    }
    public void resetUserTracker(){
        usersDAO.resetUserCounter();
    }

    public void loadProductCount(){
        Map<String, Integer> categMap = productsDAO.getCategCount();
        String bestSellingText = ordersCollection.getBestSellingProduct();
        view.displayProductInfo(categMap, bestSellingText);
    }

    public void loadUserCount(){
        Map<String, Integer> userMap = productsDAO.getUserCount();
        String newUserText = usersDAO.getNewCreatedUserID();
        view.displayUserInfo(userMap, newUserText);
    }

    public void loadSales(){
        double totalSales = productsDAO.getTotalSales();
        view.displaySales(totalSales);
    }
    public void loadBestSellingText(){
        String bestSellingText = ordersCollection.getBestSellingProduct();
        view.displayBestSelling(bestSellingText);
    }
    public void loadNewCreatedUser(){
        String newUserText = usersDAO.getNewCreatedUserID();
        view.displayNewUser(newUserText);
    }
}