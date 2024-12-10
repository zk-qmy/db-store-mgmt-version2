package products;

import customer.reviews.Reviews;

import java.util.List;

public class Products {
    private int productID;
    private String productName;
    private int stockQuantity;
    private int orderQuantity;
    private List<Reviews> reviewsList;
    private double price;

    public int getOrderQuantity(){return orderQuantity;}
    public void setOrderQuantity(int orderQuantity) {this.orderQuantity = orderQuantity;}
    public List<Reviews> getProductReviews(){return reviewsList;}
    public void setProductReviews(List<Reviews> reviewsList){
            this.reviewsList = reviewsList;}

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}