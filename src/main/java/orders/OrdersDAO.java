package orders;
/*
import connections.MySQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdersDAO {
    // Load Orders
    //// Load all
    public List<Orders> loadAllOrders(){
        List<Orders> orderList = new ArrayList<>();
        String query = """
        WITH OrderTotals AS (
            SELECT
                Orders.id AS orderID,
                Orders.customerID,
                Status.statusName,
                SUM(OrderDetails.orderQuantity * Products.price) AS orderTotal
            FROM Orders
            JOIN OrderDetails ON Orders.id = OrderDetails.orderID
            JOIN Products ON OrderDetails.productID = Products.id
            JOIN Status ON Status.id = Orders.statusID
            GROUP BY Orders.id, Orders.customerID, Status.statusName
        )
        SELECT
            ot.orderID,
            ot.customerID,
            ot.orderTotal,
            ot.statusName AS status,
            u.name AS name,
            u.address AS address,
            u.phone AS phone
        FROM OrderTotals ot
        JOIN Users u ON ot.customerID = u.id
        ORDER BY ot.orderID ASC;
        """;


        Connection conn = null;
        try {
            conn = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Orders order = new Orders();
                order.setOrderId(rs.getInt("orderID"));
                order.setCusID(rs.getInt("customerID"));
                order.setCtmName(rs.getString("name"));
                order.setTotal(rs.getDouble("OrderTotal"));
                order.setStatus(rs.getString("status"));
                order.setAddress(rs.getString("address"));
                order.setPhone(rs.getString("phone"));

                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(conn);
        }
        return orderList;
    }
    // Save an Order (already in product DAO)


    // Find an order by ID

    public List<Integer> getAllStatusID(){
        List<Integer> statusIDList = new ArrayList<>();
        Connection connection = null;
        try{
            String query = "SELECT id FROM Status";
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                statusIDList.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            return null;
        }finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return statusIDList;
    }

    //Update order status(for admin) / Cancel order (for customer)
    public boolean updateStatus(int orderID, int statusID) {
        Connection connection = null;
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "UPDATE Orders SET statusID = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, statusID);
            statement.setInt(2, orderID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // TO DO: Delete order(for admin)
    public boolean deleteOrder(int orderID){
        Connection connection = null;
        try{
            String query1 = "DELETE FROM Orders WHERE id = ?";
            String query2 = "DELETE FROM OrderDetails WHERE orderID = ?";
            connection = MySQLConnection.getInstance().getSQLConnection();

            // start transaction
            connection.setAutoCommit(false);

            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setInt(1,orderID);
            PreparedStatement statement2 = connection.prepareStatement((query2));
            statement2.setInt(1,orderID);
            statement2.executeUpdate();
            statement1.executeUpdate();

            connection.commit();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return true;
    }

    // Get all orderID
    public List<Integer> getAllorderID(){
        Connection connection = null;
        List<Integer> orderIDList = new ArrayList<>();
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "SELECT id FROM Orders";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                orderIDList.add(resultSet.getInt("id"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return orderIDList;
    }

    // Load all order of a user
    public List<Orders> loadUserOrders (int userID){
        Connection connection = null;
        List<Orders> ordersList= new ArrayList<>();
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = """
                WITH OrderTotals AS (
                    SELECT
                        Orders.id AS orderID,
                        Orders.customerID,
                        Status.statusName,
                        SUM(OrderDetails.orderQuantity * Products.price) AS orderTotal
                    FROM Orders
                    JOIN OrderDetails ON Orders.id = OrderDetails.orderID
                    JOIN Products ON OrderDetails.productID = Products.id
                    JOIN Status ON Status.id = Orders.statusID
                    GROUP BY Orders.id, Orders.customerID, Status.statusName
                )
                SELECT
                    ot.orderID,
                    ot.orderTotal,
                    ot.statusName
                FROM OrderTotals ot
                WHERE ot.customerID = ?
                ORDER BY ot.orderID ASC;
            """;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Orders order = new Orders();
                order.setOrderId(resultSet.getInt("orderID"));
                order.setTotal(resultSet.getDouble("orderTotal"));
                order.setStatus(resultSet.getString("statusName"));
                ordersList.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return ordersList;
    }

    // Get all orderID of a user
    public List<Integer> getUserOrderID (int userID){
        Connection connection = null;
        List<Integer> userOrderIDList = new ArrayList<>();
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "SELECT id from Orders WHERE customerID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                userOrderIDList.add(resultSet.getInt("id"));
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return userOrderIDList;
    }
    public String getStatusbyOrderID(int orderID) {
        Connection connection = null;
        String query = "SELECT Status.statusName FROM Status "+
                        "JOIN Orders ON Orders.statusID = Status.id " +
                        "WHERE Orders.id = ? ";
        String status = null;
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderID);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                status = resultSet.getString("statusName");
            }
        } catch (SQLException e){
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return status;
    }
}

*/