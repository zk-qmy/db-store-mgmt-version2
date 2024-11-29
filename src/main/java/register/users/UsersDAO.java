package register.users;

import connections.MySQLConnection;
import orders.Orders;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersDAO {
    // Add user (ad + cus)
    public boolean addUser(String name, String username, String password, String address, String phone, int roleID) {
        Connection connection = null;
        try {
            String query = "INSERT INTO Users(username, password, name, address, phone, roleID) VALUES (?,?,?,?,?,?)";
            connection = MySQLConnection.getInstance().getSQLConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2,password);
            statement.setString(3, name);
            statement.setString(4, address);
            statement.setString(5,phone);
            statement.setInt(6, roleID);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            MySQLConnection.getInstance().closeSQLConn(connection);
        }
        return true;
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
        try {
            connection = MySQLConnection.getInstance().getSQLConnection();
            String query = "DELETE FROM Users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userID);

            int rowsDeleted = statement.executeUpdate();

            // Check if any rows were deleted
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully: ID = " + userID);
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
        try {
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
        }
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

