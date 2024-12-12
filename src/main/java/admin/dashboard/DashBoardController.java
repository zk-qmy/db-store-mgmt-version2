package admin.dashboard;

import orders.OrdersCollection;
import products.ProductsDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class DashBoardController implements ActionListener {
    private DashBoardView view;
    private ProductsDAO productsDAO;
    private OrdersCollection ordersCollection;

    public DashBoardController ( DashBoardView view, ProductsDAO productsDAO, OrdersCollection ordersCollection){
        this.view = view;
        this.productsDAO = productsDAO;
        this.ordersCollection = ordersCollection;

        loadBestSellingText();
        loadProductCount();
        loadUserCount();
        loadSales();
        view.getBtnRefresh().addActionListener(this);
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == view.getBtnRefresh()) {
            // display report screen
            loadProductCount();
            loadUserCount();
            loadSales();
        }
    }

    public void loadProductCount(){
        Map<String, Integer> categMap = productsDAO.getCategCount();
        view.displayProductInfo(categMap);
    }

    public void loadUserCount(){
        Map<String, Integer> userMap = productsDAO.getUserCount();
        view.displayUserInfo(userMap);
    }

    public void loadSales(){
        double totalSales = productsDAO.getTotalSales();
        view.displaySales(totalSales);
    }
    public void loadBestSellingText(){
        String bestSellingText = ordersCollection.getBestSellingProduct();
        view.displayBestSelling(bestSellingText);
    }
}