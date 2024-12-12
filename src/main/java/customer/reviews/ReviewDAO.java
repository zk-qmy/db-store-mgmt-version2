package customer.reviews;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class ReviewDAO {
    private final MongoCollection<Document> reviewCollection;
    //private final MongoDatabase database;

    public ReviewDAO(MongoDatabase database) {
        this.reviewCollection = database.getCollection("reviews");
    }

    // add review
    public boolean addReview(int productID, int userID, ObjectId orderID, String reviewText) {
        try {
            Document review = new Document ("productID", productID)
                    .append("userID", userID)
                    .append("orderID", orderID)
                    .append("reviewText", reviewText)
                    .append("timestamp",  new Date());
            reviewCollection.insertOne(review);
            System.out.println("Review added to MongoDb!");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // load review based on an order of a customer (orderID, and userID)
    public List<Reviews> loadReviewByOrder(ObjectId orderID, int userID) {
        //MongoDatabase database = null;
        List<Reviews> reviewByOrderList = new ArrayList<>();
        try {
            List<Document> reviewDocs = reviewCollection.find(Filters.and(
                    Filters.eq("orderID", orderID),
                    Filters.eq("userID", userID)
            )).into(new ArrayList<>());

            // Map docs with objects
            for (Document doc : reviewDocs) {
                Reviews review = new Reviews();
                review.setReviewID(doc.getObjectId("_id"));
                review.setOrderID(doc.getObjectId("orderID"));
                review.setProductID(doc.getInteger("productID"));
                review.setCustomerID(doc.getInteger("userID"));
                review.setReviewText(doc.getString("reviewText"));
                review.setTimestamp(doc.getDate("timestamp"));

                reviewByOrderList.add(review);
            }

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
            for (Map.Entry<ObjectId, String> entry : collectedReviews.entrySet()) {
                String updatedText = entry.getValue();
                ObjectId reviewID = entry.getKey();
                // Create update query to find the document using productID and currentOrderID
                Document updateQuery = new Document("_id", reviewID);
                Document updateFields = new Document("$set", new Document("reviewText", updatedText));
                reviewCollection.updateOne(updateQuery, updateFields);
                System.out.println("Updated review!");
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    // delete review
    public boolean deleteReview(ObjectId orderID){
        try{
            Document filter = new Document("orderID", orderID);
            // Attempt to delete the document
            long deletedCount = reviewCollection.deleteOne(filter).getDeletedCount();
            if (deletedCount > 0) {
                System.out.println("Review with orderID " + orderID + " has been deleted.");
                return true;
            } else {
                System.out.println("No review found with orderID " + orderID);
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}