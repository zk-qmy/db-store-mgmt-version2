package orders;

//import connections.MongoDbConnection;
import connections.MySQLConnection;
import customer.cart.Cart;
import customer.cart.CartLines;

import java.sql.*;

public class OrderDetailsDAO {
    private Connection connection;
    public OrderDetailsDAO() {
    }
    // Load all (OrderDetails)
    // Find an OrderDetail
    // Save an OrderDetail
    // addToCart
    public boolean addToOrderDB(Cart cart, int defaultStatusID, int customerID) {
        System.out.println("Got into addToOrderDB function!!!");
        connection = null;
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Orders(customerID, statusID) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, customerID);
            stmt.setInt(2, defaultStatusID);

            stmt.executeUpdate();
            // Retrieve generated OrderID
            ResultSet generatedKey = stmt.getGeneratedKeys();
            int orderID = 0;
            if (generatedKey.next()) {
                orderID = generatedKey.getInt(1);
            }
            stmt.close();

            stmt = connection.prepareStatement("INSERT INTO OrderDetails(orderID, productID, orderQuantity) VALUES (?, ?, ?)");
            for (CartLines line : cart.getLines()) {
                stmt.setInt(1, orderID);
                stmt.setInt(2, line.getProductID());
                stmt.setInt(3, line.getOrderQuantity());
                stmt.executeUpdate();
            }
            stmt.close();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        // Remove product(an order) from Cart
    }
}
