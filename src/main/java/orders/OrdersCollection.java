package orders;

import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import customer.cart.CartLines;
import org.bson.Document;
import org.bson.types.ObjectId;
import register.users.Users;

import java.util.ArrayList;
import java.util.List;

public class OrdersCollection{
    private final MongoCollection<Document> ordersCollection;

    public OrdersCollection(MongoDatabase database){
        this.ordersCollection = database.getCollection("orders");
    }
    // Load all orders
    public List<Orders> loadAllOrders() {
        List<Orders> ordersList = new ArrayList<>();
        try {
            // Retrieve all documents from the collection
            FindIterable<Document> orderDocs = ordersCollection.find();

            for (Document orderDoc : orderDocs) {
                // print out data to debug
                System.out.println(orderDoc.toJson());


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
                        orderDetails.setOrderQuantity(detailDoc.getInteger("orderQuantity")); // Corrected field name from "quantity" to "orderQuantity"
                        orderDetails.setCost(detailDoc.getDouble("cost"));
                        orderDetailsList.add(orderDetails);
                    }
                }
                order.setOrderLines(orderDetailsList);
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

            autoID = (ObjectId) orderDoc.get("_id");
            System.out.println("Auto generated ID: " + autoID);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return autoID;
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
    public void deleteOrder( ObjectId orderID){

    }
}