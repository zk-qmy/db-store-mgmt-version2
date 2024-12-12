package register.users;

import connections.MySQLConnection;
import connections.RedisConnection;
import orders.Orders;
import redis.clients.jedis.UnifiedJedis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {
    private UnifiedJedis jedis;
    public UsersDAO(RedisConnection redisConnection){
        this.jedis = redisConnection.getJedis();
    }
    // Add user (ad + cus)
    public boolean addUser(String name, String username, String password, String address, String phone, int roleID) {
        Connection connection = null;
        try {
            String query = "INSERT INTO Users(username, password, name, address, phone, roleID) VALUES (?,?,?,?,?,?)";
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, username);
            statement.setString(2,password);
            statement.setString(3, name);
            statement.setString(4, address);
            statement.setString(5,phone);
            statement.setInt(6, roleID);
            statement.executeUpdate();

            // Add userID to Redis if the user has the role of customer (roleID = 2)
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    int newUserID = rs.getInt(1);  // Get the generated user ID
                    if (roleID == 2) {  // Only track customers
                        jedis.rpush("user_ids", String.valueOf(newUserID));
                        System.out.println("Most recent Customer Created: ID = " + newUserID);
                    }
                    return true;  // User added successfully
                }
            }catch ( SQLException e){
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return true;
    }
    public String getNewCreatedUserID(){
        List<String> userIDList = jedis.lrange("user_ids", -1, -1);
        // Check if the list is not empty and print the latest element
        String latestUserID = "No user yet!";
        if (!userIDList.isEmpty()) {
            latestUserID = userIDList.get(0);
            System.out.println("Latest user ID added: " + userIDList.get(0));
        } else {
            System.out.println("The list is empty.");
        }
        return latestUserID;
    }
    public void resetUserCounter() {
        try {
            // Delete the "user_ids" list from Redis to reset it
            jedis.del("user_ids");
            System.out.println("User IDs list has been reset.");
        } catch (Exception e) {
            System.out.println("Error resetting the user IDs list in Redis.");
            e.printStackTrace();
        }
    }
    // Find user (ad)
    public Users loadUser(String username, String password) {
        Connection connection = null;
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Users user = new Users();
                user.setUserID(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setAddress(resultSet.getString("address"));
                user.setPhoneNum(resultSet.getString("phone"));
                user.setRoleID(resultSet.getInt("roleID"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Database access error!");
            e.printStackTrace();
        }
        return null;
    }
    // Update user (ad)
    public boolean updateUser(int userID, String name, String password, String address, String phone, int roleID) {
        Connection connection = null;
        try {
            String query = "UPDATE Users SET password=?, name=?, address=?, phone=?, roleID=? WHERE id= ?";
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            //statement.setString(1, username);
            statement.setString(1,password);
            statement.setString(2, name);
            statement.setString(3, address);
            statement.setString(4,phone);
            statement.setInt(5, roleID);
            statement.setInt(6, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return true;
    }
    // delete user (ad)
    public boolean deleteUser(int userID) {
        Connection connection = null;
        //String lastCreatedUserID = jedis.get("last_created_userID");
        List<String> userIDList = jedis.lrange("user_ids", -1, -1);
        // Check if the list is not empty and print the latest element
        String lastCreatedUserID = null;
        if (!userIDList.isEmpty()) {
            lastCreatedUserID = userIDList.get(0);
            System.out.println("Latest user ID added: " + userIDList.get(0));
        } else {
            System.out.println("The list is empty.");
        }

        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "DELETE FROM Users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);
            statement.executeUpdate();

            System.out.println("User deleted successfully: ID = " + userID);
            // Check if the deleted user is the last created user
            if (String.valueOf(userID).equals(lastCreatedUserID)) {
                jedis.lrem("user_ids", 0, String.valueOf(userID));
                /*System.out.println("Removed last created UserID from Redis because it was deleted.");
                // DEBUG
                List<String> updatedList = jedis.lrange("user_ids", 0, -1);
                System.out.println("Updated user IDs list: " + updatedList);*/
            return true;
            } else {
                System.out.println("No user found with ID: " + userID);
                return false;
            }
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
    }

    // Get all userID
    public List<Integer> getAllUserID() {
        List<Integer> userIDList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = " SELECT id FROM Users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                userIDList.add(resultSet.getInt("id"));
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return  null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return userIDList;
    }

    // Get user by username
    public Users getUserByUsername(String username) {
        Connection connection = null;
        Users user = null;

        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query1 = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query1);
            statement.setString(1, username);

            // Debug: print the SQL query
            //System.out.println("Executing query1: " + statement.toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                user = new Users();
                user.setUserID(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setUsername(resultSet.getString("username"));
                user.setAddress(resultSet.getString("address"));
                user.setPhoneNum(resultSet.getString("phone"));
                user.setPassword(resultSet.getString("password"));
                user.setRoleID(resultSet.getInt("roleID"));
            }

            resultSet.close();
            statement.close();

            if (user != null) {
                String query2 = "SELECT roleName FROM Roles WHERE id = ?";
                PreparedStatement roleStatement = connection.prepareStatement(query2);
                roleStatement.setInt(1, user.getRoleID());  // Set the parameter here

                // Debug: print the SQL query
                //System.out.println("Executing query2: " + roleStatement.toString());

                ResultSet roleResultSet = roleStatement.executeQuery();

                if (roleResultSet.next()) {
                    user.setRoleName(roleResultSet.getString("roleName"));
                }
                roleResultSet.close();
                roleStatement.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }

        return user;
    }

    public List<Users> loadAllUsers() {
        Connection connection =null;
        List<Users> userList = new ArrayList<>();
        try {
            String query = "SELECT * FROM Users JOIN Roles ON Users.roleID = Roles.id";
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                Users user = new Users();
                user.setUserID(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setAddress(resultSet.getString("address"));
                user.setPhoneNum(resultSet.getString("phone"));
                user.setRoleName(resultSet.getString("roleName"));
                userList.add(user);
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return userList;
    }

    public List<Orders> getProcessOrders(int userID) {
        Connection connection = null;
        String query = "SELECT Users.id AS userID, Users.username, Status.statusName, Orders.id AS orderID "+
                    "FROM Orders "+
                    "JOIN Status ON Orders.statusID = Status.id "+
                    "JOIN Users ON Orders.customerID = Users.id "+
                    "WHERE Users.id = ?"; //AND Status.statusName NOT IN ('cancelled', 'shipped') ";

        List<Orders> ordersList = new ArrayList<>();
        /*try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1,userID);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Orders order = new Orders();
                order.setOrderId(resultSet.getInt("orderID"));
                order.setStatus(resultSet.getString("statusName"));
                ordersList.add(order);
            }
        } catch (SQLException e) {
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }*/
        return ordersList;
    }

    public List<String> getAllUsernames(){
        List<String> usernameList = new ArrayList<>();
        Connection connection = null;
        String query = "SELECT username FROM Users";
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                usernameList.add(resultSet.getString("username"));
            }
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return usernameList;
    }



}

