package customer.cart;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private int cartID;
    private double totalPayment;

    private List<CartLines> lines;
    public Cart() {
        lines = new ArrayList<>();
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public int getCartID() {
        return cartID;
    }

    public List<CartLines> getLines() {
        return lines;
    }
    public void addLine(CartLines line) {
        lines.add(line);
    }
    public void removeLine(CartLines line) {
        lines.remove(line);
    }
}