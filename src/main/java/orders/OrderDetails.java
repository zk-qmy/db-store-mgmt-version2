package orders;
public class OrderDetails {
    private int id; // orderDetails id
    private int orderID;
    private int productID;
    private int orderQuantity;
    private double cost;

    public void setId(int id) {
        this.id = id;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public int getProductID() {
        return productID;
    }

    public int getOrderID() {
        return orderID;
    }

    public double getCost() {
        return cost;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }
}