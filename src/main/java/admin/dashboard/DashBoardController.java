package admin.dashboard;

import products.ProductsDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class DashBoardController implements ActionListener {
    private DashBoardView view;
    private ProductsDAO productsDAO;
    public DashBoardController ( DashBoardView view, ProductsDAO productsDAO){
        this.view = view;
        this.productsDAO = productsDAO;

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
}