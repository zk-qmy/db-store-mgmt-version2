package utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import connections.MongoDbConnection;
import connections.MySQLConnection;
import org.bson.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataMigration {
    public static void main(String[] args) {
        // Connect to MySQL
        Connection sqlConnection = null;
        try {
            sqlConnection = MySQLConnection.getInstance().getSQLConnection();
        } catch (Exception e){
            e.printStackTrace();
        }
        // Connect to MongoDB
       MongoDatabase mongoDatabase = null;
       try{
           mongoDatabase = MongoDbConnection.getInstance().getMongoDatabase("db-project2");
           // Migrate Users
            //migrateUsers(sqlConnection, mongoDatabase);
            // Migrate Orders
            migrateOrders(sqlConnection, mongoDatabase);
       } catch (Exception e) {
            e.printStackTrace();
       } finally {
            MongoDbConnection.getInstance().closeMongoConn();
            MySQLConnection.getInstance().closeSQLConn(sqlConnection);
       }
    }

    private static void migrateUsers(Connection sqlConnection, MongoDatabase mongoDatabase){
        String usersQuery = """
            SELECT u.id, u.username, u.password, u.name, u.address, u.phone, r.roleName
            FROM Users u
            JOIN Roles r ON u.roleID = r.id
        """;

        MongoCollection<Document> usersCollection = mongoDatabase.getCollection("userProfiles");

        try (Statement statement = sqlConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(usersQuery)) {

            while (resultSet.next()) {
                Document userDoc = new Document()
                        .append("userID", resultSet.getInt("id"))
                        //.append("username", resultSet.getString("username"))
                        //.append("password", resultSet.getString("password"))
                        .append("name", resultSet.getString("name"))
                        .append("address", resultSet.getString("address"))
                        .append("phone", resultSet.getString("phone"))
                        .append("role", resultSet.getString("roleName"));

                usersCollection.insertOne(userDoc);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        System.out.println("Users migrated successfully!");
    }

    private static void migrateOrders(Connection sqlConnection, MongoDatabase mongoDatabase){
        String ordersQuery = """
            SELECT o.id, o.customerID, s.statusName, u.username, u.name
            FROM Orders o
            JOIN Status s ON o.statusID = s.id
            JOIN Users u ON o.customerID = u.id
        """;

        MongoCollection<Document> ordersCollection = mongoDatabase.getCollection("orders");

        try (Statement orderStatement = sqlConnection.createStatement();
             ResultSet orderResultSet = orderStatement.executeQuery(ordersQuery)) {

            while (orderResultSet.next()) {
                int orderId = orderResultSet.getInt("id");

                // Fetch OrderDetails
                String orderDetailsQuery = """
                    SELECT od.productID, p.name as productName, od.orderQuantity
                    FROM OrderDetails od
                    JOIN Products p ON od.productID = p.id
                    WHERE od.orderID = ?
                """;
                List<Document> orderDetailsList = new ArrayList<>();
                try (PreparedStatement detailsStatement = sqlConnection.prepareStatement(orderDetailsQuery)) {
                    detailsStatement.setInt(1, orderId);
                    try (ResultSet detailsResultSet = detailsStatement.executeQuery()) {
                        while (detailsResultSet.next()) {
                            Document detailDoc = new Document()
                                    .append("productID", detailsResultSet.getInt("productID"))
                                    .append("productName", detailsResultSet.getString("productName"))
                                    .append("orderQuantity", detailsResultSet.getInt("orderQuantity"));
                            orderDetailsList.add(detailDoc);
                        }
                    }
                }

                // Create Order Document
                Document orderDoc = new Document()
                        //.append("_id", orderId)
                        .append("userID", orderResultSet.getInt("customerID"))
                        /*.append("customer", new Document()
                                .append("id", orderResultSet.getInt("customerID"))
                                .append("username", orderResultSet.getString("username"))
                                .append("name", orderResultSet.getString("name")))*/
                        .append("status", orderResultSet.getString("statusName"))
                        .append("orderDetails", orderDetailsList);

                ordersCollection.insertOne(orderDoc);
            }
            System.out.println("Orders migrated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
