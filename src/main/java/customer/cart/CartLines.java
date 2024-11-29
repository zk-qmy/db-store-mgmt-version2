package customer.cart;

public class CartLines {
    private int cartID;
    private int ctmID;
    private String sessionID;
    private int productID;
    private int orderQuantity;
    private double cost;

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCtmID(int ctmID) {
        this.ctmID = ctmID;
    }

    public int getCtmID() {
        return ctmID;
    }
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getProductID() {
        return productID;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }
}