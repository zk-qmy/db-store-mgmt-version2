package customer.reviews;

import org.bson.types.ObjectId;

import java.util.Date;

public class Reviews {
    private ObjectId reviewID;
    private int productID;
    private ObjectId orderID;
    private int customerID;
    private String reviewText;
    private Date timestamp;

    public ObjectId getReviewID(){return reviewID;}
    public int getProductID() {return productID;}
    public ObjectId getOrderID(){return orderID;}
    public int getCustomerID(){return customerID;}
    public String getReviewText(){return reviewText;}
    public Date getTimestamp(){return timestamp;}

    public void setReviewID(ObjectId reviewID) {
        this.reviewID = reviewID;
    }
    public void setProductID(int productID) {
        this.productID = productID;
    }
    public void setOrderID(ObjectId orderID) {
        this.orderID = orderID;
    }
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}