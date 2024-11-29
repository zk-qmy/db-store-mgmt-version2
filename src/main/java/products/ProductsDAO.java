package products;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import connections.MySQLConnection;



public class ProductsDAO {
    // Load
    //// Load all
    public List<Products> loadAllProducts(){
        List<Products> productsList = new ArrayList<>();
        String query = "SELECT * FROM Products";
        Connection conn = null;
        try {
            conn = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Products product = new Products();
                product.setProductID(rs.getInt("id"));
                product.setProductName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setStockQuantity(rs.getInt("quantity"));
                productsList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(conn);
        }
        return productsList;
    }
    //// Load by ID
    public Products findProductbyID(int id) {
        String query = "SELECT * FROM Products WHERE id = ?";
        Connection connection = null;
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            Products product = null;
            if (rs.next()) {
                product = new Products();
                product.setProductID(rs.getInt("id"));
                product.setProductName(rs.getString("name"));
                product.setStockQuantity(rs.getInt("quantity"));
                product.setPrice(rs.getDouble("price"));
            }
            MySQLConnection.getInstance().closeSQLConn(connection);
            return product;
        } catch (SQLException e) {
            System.out.println("DB access error");
            e.printStackTrace();
        }
        return null;
    }


    // Add
    public boolean addProductToDB(String productName, int quantity, double price, int categID){
        Connection connection= null;
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "INSERT INTO Products(name, quantity, price, categoryid) VALUES (?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, productName);
            statement.setInt(2, quantity);
            statement.setDouble(3, price);
            statement.setInt(4, categID);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return true;

    }

    // Update
    public boolean updateProductToDB(int productID, String productName, int quantity, double price, int categID) {
        Connection connection= null;
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "UPDATE Products SET name = ?, quantity = ?, price = ?, categoryid = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, productName);
            statement.setInt(2, quantity);
            statement.setDouble(3, price);
            statement.setInt(4, categID);
            statement.setInt(5, productID);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return true;
    }

    // delete
    public boolean deleteProductFromDB(int productID) {
        Connection connection = null;
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "DELETE FROM Products WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, productID);

            int rowsDeleted = statement.executeUpdate();

            // Check if any rows were deleted
            if (rowsDeleted > 0) {
                System.out.println("Product deleted successfully: ID = " + productID);
                return true;
            } else {
                System.out.println("No product found with ID: " + productID);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
    }
    //// Get all ID of the products that appear in orders
    public List<Integer> getOrderedProductID(){
        Connection connection = null;
        List<Integer> orderedProductID = new ArrayList<>();
        String query = "SELECT DISTINCT p.id AS productID " +
                 "FROM products p " +
                 "JOIN orderDetails od ON p.id = od.productID";
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                orderedProductID.add(resultSet.getInt("productID"));
            }

        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return orderedProductID;
    }

    // Get product id key
    public List<Integer> getProductKeyID() {
        Connection connection = null;
        List<Integer> productKeys = new ArrayList<>();
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT id FROM Products");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                //Products product = new Products();
                //product.setProductID(rs.getInt("id"));
                productKeys.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return productKeys;
    }
    // Get all category tags
    public List<String> getAllCategs(){
        List<String> categories = new ArrayList<>();
        categories.add("Start filtering"); // set default(1st option) view of combobox
        Connection connection = null;
        try {
            connection= MySQLConnection.getInstance().getSQLConnection();
            String query = "SELECT name FROM Categories";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                //int categID = rs.getInt("id");
                //String categName = rs.getString("name");
                categories.add(rs.getString("name"));
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return categories;
    }

    // Get all Product with a certain categ name
    public List<Products> findProductsByCateg(String categName){
        Connection connection=null;
        List<Products> productsList = new ArrayList<>();
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "SELECT * FROM Products JOIN Categories ON Products.categoryID = Categories.id WHERE Categories.name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, categName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Products product = new Products();
                product.setProductID(rs.getInt("id"));
                product.setProductName(rs.getString("name"));
                product.setStockQuantity(rs.getInt("quantity"));
                product.setPrice(rs.getDouble("price"));
                productsList.add(product);
            }

        }catch (SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return productsList;
    }

    // Update Stock of a product with id
    public void updateProductStock(int newStockQuantity, int productID){
        Connection connection = null;
        try {
            String query = "UPDATE Products SET quantity = ? WHERE id = ?";
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, newStockQuantity);
            statement.setInt(2, productID);
            // Execute the update query
            int rowsUpdated = statement.executeUpdate();

            // check if the update was successful
            if (rowsUpdated > 0) {
                System.out.println("Stock updated successfully for product ID: " + productID);
            } else {
                System.out.println("No product found with ID: " + productID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
    }
    // Get all product ID
    public List<Integer> getAllProductID(){
        Connection connection = null;
        List<Integer> productIDList = new ArrayList<>();
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "SELECT id FROM Products";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                productIDList.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return productIDList;
    }

    // Get all categID
    public List<Integer> getAllCategID(){
        Connection connection = null;
        List<Integer> productIDList = new ArrayList<>();
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "SELECT id FROM Categories";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                productIDList.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return productIDList;
    }

    // get products that have stock quantity smaller than 5
    public List<Products> getAlertProducts (){
        Connection connection = null;
        List<Products> alertProductList = new ArrayList<>();
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = " SELECT * FROM Products WHERE quantity <= 5";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Products product = new Products();
                product.setProductID(resultSet.getInt("id"));
                product.setProductName(resultSet.getString("name"));
                product.setStockQuantity(resultSet.getInt("quantity"));
                alertProductList.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return alertProductList;
    }
    public Map<String, Integer> getCategCount() {
        Map<String, Integer> categMap = new HashMap<>();
        String query = "SELECT c.name, COUNT(p.id) AS totalCategCount " +
                        "FROM categories c " +
                        "JOIN products p ON c.id = p.categoryID " +
                        "GROUP BY c.Name";
        Connection connection = null;
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                categMap.put(resultSet.getString("name"),
                            resultSet.getInt("totalCategCount"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return categMap;
    }

    public Map<String, Integer> getUserCount(){
        Connection connection = null;
        String query = "SELECT r.roleName, COUNT(u.id) AS totalUserCount " +
                        "FROM Users u " +
                        "JOIN roles r ON r.id = u.roleID " +
                        "GROUP BY r.roleName";
        Map<String, Integer> userMap = new HashMap<>();
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                userMap.put(resultSet.getString("roleName"),
                        resultSet.getInt("totalUserCount"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return userMap;
    }

    public double getTotalSales(){
        Connection connection = null;
        String query = "SELECT SUM(o.orderQuantity * p.price) as totalSale " +
                        "FROM OrderDetails o " +
                        "JOIN Products p ON o.productID = p.id "+
                        "JOIN Orders od ON o.orderID=od.id " +
                        "WHERE od.statusID=4 ";
        double total = 0;
        try{
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                total = resultSet.getDouble("totalSale");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return total;
    }
}