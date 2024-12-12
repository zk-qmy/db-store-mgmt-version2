package orders;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import connections.RedisConnection;
import customer.cart.CartLines;
import org.bson.Document;
import org.bson.types.ObjectId;
import redis.clients.jedis.UnifiedJedis;
import register.users.Users;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class OrdersCollection{
    private final MongoCollection<Document> ordersCollection;
    private final MongoCollection<Document> userProfilesCollection;
    //private final RedisConnection redisConnection;
    private final UnifiedJedis jedis;

    public OrdersCollection(MongoDatabase database, RedisConnection redisConnection){
        this.ordersCollection = database.getCollection("orders");
        this.userProfilesCollection = database.getCollection("userProfiles");
        //this.redisConnection = redisConnection;
        this.jedis = redisConnection.getJedis();
    }
    // Load all orders
    public List<Orders> loadAllOrders() {
        List<Orders> ordersList = new ArrayList<>();
        try {
            // Retrieve all documents from the orders collection
            FindIterable<Document> orderDocs = ordersCollection.find();

            for (Document orderDoc : orderDocs) {
                // DEBUG: print out data to debug
                //System.out.println(orderDoc.toJson());

                Orders order = new Orders();
                // Set order details from the document
                order.setOrderId(orderDoc.getObjectId("_id"));
                order.setStatus(orderDoc.getString("status"));
                order.setUserID(orderDoc.getInteger("userID"));
                order.setTotal(orderDoc.getDouble("total"));

                // Retrieve the orderDetails subdocument array from the document
                List<Document> orderDetailDocs = orderDoc.getList("orderDetails", Document.class);
                List<OrderDetails> orderDetailsList = new ArrayList<>();

                // If orderDetails are present, process them
                if (orderDetailDocs != null) {
                    for (Document detailDoc : orderDetailDocs) {
                        OrderDetails orderDetails = new OrderDetails();
                        orderDetails.setProductID(detailDoc.getInteger("productID"));
                        orderDetails.setOrderQuantity(detailDoc.getInteger("orderQuantity"));
                        orderDetails.setCost(detailDoc.getDouble("cost"));
                        orderDetailsList.add(orderDetails);
                    }
                }
                order.setOrderLines(orderDetailsList);
                // Retrieve customer info from userProfiles collection
                Document userDoc = userProfilesCollection.find(Filters.eq("userID", order.getUserID())).first();
                if (userDoc != null) {
                    order.setCtmName(userDoc.getString("name"));
                    order.setAddress(userDoc.getString("address"));
                    order.setPhone(userDoc.getString("phone"));
                } else {
                    System.out.println("No user data found for userID: " + order.getUserID());
                }
                // Add the populated order to the orders list
                ordersList.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ordersList;
    }

    // Load orders by userID
    public List<Orders> loadUserOrders(int userID) {
        List<Orders> userOrders = new ArrayList<>();
        try {
            List<Document> ordersDocuments = ordersCollection.find(Filters.eq("userID", userID)).into(new ArrayList<>());
            for (Document orderDoc : ordersDocuments) {
                Orders order = new Orders();
                order.setOrderId(orderDoc.getObjectId("_id"));
                order.setUserID(orderDoc.getInteger("userID"));
                order.setStatus(orderDoc.getString("status"));
                order.setTotal(orderDoc.getDouble("total"));

                List<Document> orderDetailsDocuments = orderDoc.getList("orderDetails", Document.class);
                List<OrderDetails> orderDetailsList = new ArrayList<>();
                for (Document detailDoc : orderDetailsDocuments) {
                    OrderDetails orderDetails = new OrderDetails();
                    orderDetails.setProductID(detailDoc.getInteger("productID"));
                    orderDetails.setOrderQuantity(detailDoc.getInteger("orderQuantity"));
                    orderDetails.setCost(detailDoc.getDouble("cost"));
                    orderDetailsList.add(orderDetails);
                }
                order.setOrderLines(orderDetailsList);
                userOrders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userOrders;
    }
    // Load an order by orderID
    public Orders loadOrdersByID(ObjectId orderID){
        if (!(orderID instanceof ObjectId)) {
            System.out.println("The provided orderID is not of type ObjectId.");
            return null;
        }
        Document orderDoc = null;
        try{
            orderDoc = ordersCollection.find(Filters.eq("_id", orderID)).first();
            if (orderDoc == null) {
                System.out.println("No order found with Order ID: " + orderID);
                return null;
            }

            Orders order = new Orders();
            order.setOrderId(orderDoc.getObjectId("_id"));
            order.setUserID(orderDoc.getInteger("userID"));
            order.setStatus(orderDoc.getString("status"));

            List<Document> orderDetailsDocuments = orderDoc.getList("orderDetails", Document.class);
            List<OrderDetails> orderDetailsList = new ArrayList<>();
            for (Document detailDoc : orderDetailsDocuments) {
                OrderDetails orderDetails = new OrderDetails();
                orderDetails.setProductID(detailDoc.getInteger("productID"));
                orderDetails.setOrderQuantity(detailDoc.getInteger("orderQuantity"));
                orderDetails.setCost(detailDoc.getDouble("cost"));
                orderDetailsList.add(orderDetails);
            }
            order.setOrderLines(orderDetailsList);
            order.setTotal(orderDoc.getDouble("total"));
            return order;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Add order
    public ObjectId addOrderNGetID(int userID, double totalPayment, List<CartLines> cartLinesList, ClientSession mongoSession){
        //int orderID = 0;
        ObjectId autoID = null;
        try {
            //orderID = CustomMGIdGenerator.getInstance().getNextId("orders", database);
            Document orderDoc = new Document("userID", userID)
                    .append("status", "pending") // add pending as default status
                    .append("total", totalPayment)
                    .append("orderDetails", cartLinesList.stream().map(line -> new Document("productID", line.getProductID())
                            .append("orderQuantity", line.getOrderQuantity())
                            .append("cost", line.getCost())).toList());

            //MongoCollection<Document> ordersCollection = database.getCollection("orders");
            ordersCollection.insertOne(mongoSession, orderDoc);

            // update redis sales
            updateRedisSales(jedis, orderDoc);

            autoID = (ObjectId) orderDoc.get("_id");
            System.out.println("Auto generated ID: " + autoID);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return autoID;
    }
    // Update Redis sale count
    private void updateRedisSales(UnifiedJedis jedis, Document order){
        List<Document> orderDetails = (List<Document>) order.get("orderDetails");
        for (Document orderDetail : orderDetails) {
            int productID = orderDetail.getInteger("productID");
            int quantity = orderDetail.getInteger("orderQuantity");
            // increase count
            jedis.hincrBy("product_sales", String.valueOf(productID), quantity);
            // Retrieve and print the updated sales count
            String updatedCount = jedis.hget("product_sales", String.valueOf(productID));
            System.out.println("Product ID: " + productID + ", Sales Count: " + updatedCount);
        }
    }
    public void resetBestSelling(){
        try {
            jedis.del("product_sales");  // Delete the entire hash in Redis
            System.out.println("All product sales have been reset.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get bestselling product
    public String getBestSellingProduct() {
        String bestSellingProduct = jedis.hgetAll("product_sales").entrySet().stream()
                .max((entry1, entry2) -> Integer.compare(
                        Integer.parseInt(entry1.getValue()),
                        Integer.parseInt(entry2.getValue())))
                .map(entry -> "Product ID: " + entry.getKey() + ", Sales: " + entry.getValue())
                .orElse("No products sold yet");

        System.out.println("Best-Selling Product: " + bestSellingProduct);
        return bestSellingProduct;
    }
    // Get all orderID of a user
    public List<ObjectId> getUserOrderID(int userID){
        List<ObjectId> orderIdList = new ArrayList<>();
        try{
            for (Document orderDoc : ordersCollection.find()){
                ObjectId orderID = orderDoc.getObjectId("_id");
                orderIdList.add(orderID);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return orderIdList;
    }
    // Update Order Status
    public boolean updateStatus(ObjectId orderID, String newStatus){
        try{
            ordersCollection.updateOne(
                    Filters.eq("_id", orderID),
                    Updates.set("status", newStatus)
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Delete Order
    public boolean deleteOrder( ObjectId orderID){
        try{
            DeleteResult result = ordersCollection.deleteOne(Filters.eq("_id", orderID));
            // Check if a document was deleted
            if (result.getDeletedCount() > 0) {
                System.out.println("Order with ID " + orderID + " was deleted successfully.");
                return true;
            } else {
                System.out.println("No order found with ID " + orderID);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}