package customer.reviews;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import connections.MongoDbConnection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewDAO {
    MongoDatabase database;
    public ReviewDAO(MongoDatabase database) {
        this.database = database != null? database : MongoDbConnection.getInstance().getMongoDatabase("db-project2");
    }
    // add review
    public boolean addReview(String productID, String userID, String orderID, String reviewText) {
        //MongoDatabase database = null;
        try {
            //database = MongoDbConnection.getInstance().getMongoDatabase("db-project2");
            MongoCollection<Document> collection = database.getCollection("reviews");
            Document review = new Document("productID", productID)
                    .append("userID", userID)
                    .append("orderID", orderID)
                    .append("reviewText", reviewText)
                    .append("timestamp", System.currentTimeMillis());
            collection.insertOne(review);
            System.out.println("Review added to MongoDb!");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    // load review based on an order of a customer (orderID, and userID)
    public List<Document> loadReviewByOrder(int orderID, int userID) {
        //MongoDatabase database = null;
        List<Document> reviewByOrderList = new ArrayList<>();
        try {
            //database = MongoDbConnection.getInstance().getMongoDatabase("db-project2");
            MongoCollection<Document> collection = database.getCollection("reviews");
            collection.find(Filters.and(
                    Filters.eq("orderID", orderID),
                    Filters.eq("userID", userID)
            )).into(reviewByOrderList);
        } catch (Exception e){
            e.printStackTrace();
        }
        return reviewByOrderList;
    }
    // load review based on productID (admin)
    // load review based on customerID (admin)
    // update review
    public boolean updateReviews(Map<ObjectId, String> collectedReviews) {
        try {
            MongoCollection<Document> collection = database.getCollection("reviews");

            for (Map.Entry<ObjectId, String> entry : collectedReviews.entrySet()) {
                String updatedText = entry.getValue();
                ObjectId reviewID = entry.getKey();
                // Create update query to find the document using productID and currentOrderID
                Document updateQuery = new Document("_id", reviewID);
                Document updateFields = new Document("$set", new Document("reviewText", updatedText));
                collection.updateOne(updateQuery, updateFields);
                System.out.println("Updated review!");
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    // delete review
}